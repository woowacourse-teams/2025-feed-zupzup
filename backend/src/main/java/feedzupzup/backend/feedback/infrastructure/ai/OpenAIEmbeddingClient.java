package feedzupzup.backend.feedback.infrastructure.ai;

import feedzupzup.backend.global.exception.InfrastructureException.RestClientServerException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAIEmbeddingClient {

    private final OpenAIProperties openAIProperties;
    private final RestClient openAiEmbeddingRestClient;
    private final OpenAIErrorHandler openAIErrorHandler;

    public double[] extractEmbedding(String text) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "input", text,
                    "model", openAIProperties.getEmbeddingModel()
            );
            log.info("[OpenAI] 임베딩 요청 시작 - model: {}, input: {}",
                    openAIProperties.getEmbeddingModel(),
                    summarizeInput(text));

            OpenAIEmbeddingResponse response = openAiEmbeddingRestClient.post()
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, openAIErrorHandler::handleError)
                    .body(OpenAIEmbeddingResponse.class);

            double[] embedding = extractEmbeddingOrThrow(response);

            log.info("[OpenAI] 임베딩 요청 성공 - inputLength: {}, embeddingLength: {}",
                    text.length(),
                    embedding.length
            );

            return embedding;
        } catch (Exception e) {
            log.error("임베딩 생성 실패: {}", e.getMessage(), e);
            throw new RestClientServerException("임베딩 생성 중 오류 발생", e);
        }
    }

    private static double[] extractEmbeddingOrThrow(final OpenAIEmbeddingResponse response) {
        return Optional.ofNullable(response)
                .map(OpenAIEmbeddingResponse::getData)
                .filter(data -> !data.isEmpty())
                .map(data -> data.getFirst().getEmbedding())
                .orElseThrow(() -> new RestClientServerException("임베딩 데이터가 비어 있거나 응답이 없습니다"));
    }

    // 로그용
    private String summarizeInput(String text) {
        int maxLen = 50;
        if (text == null) return "null";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + "...(" + text.length() + " chars)";
    }
}
