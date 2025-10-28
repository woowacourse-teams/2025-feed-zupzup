package feedzupzup.backend.feedback.infrastructure.embedding;

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
public class VoyageAIEmbeddingClient {

    private final VoyageAIProperties voyageAIProperties;
    private final RestClient voyageAiEmbeddingRestClient;
    private final VoyageAIErrorHandler voyageAIErrorHandler;

    public double[] extractEmbedding(final String text) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "input", text,
                    "model", voyageAIProperties.getEmbeddingModel(),
                    "input_type", "document"
            );
            log.info("[VoyageAI] 임베딩 요청 시작 - model: {}, input: {}",
                    voyageAIProperties.getEmbeddingModel(),
                    summarizeInput(text));

            VoyageAIEmbeddingResponse response = voyageAiEmbeddingRestClient.post()
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, voyageAIErrorHandler::handleError)
                    .body(VoyageAIEmbeddingResponse.class);

            double[] embedding = extractEmbeddingOrThrow(response);

            log.info("[VoyageAI] 임베딩 요청 성공 - inputLength: {}, embeddingLength: {}, totalTokens: {}",
                    text.length(),
                    embedding.length,
                    response.getUsage().getTotalTokens()
            );

            return embedding;
        } catch (Exception e) {
            log.error("임베딩 생성 실패: {}", e.getMessage(), e);
            throw new RestClientServerException("임베딩 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private double[] extractEmbeddingOrThrow(final VoyageAIEmbeddingResponse response) {
        return Optional.ofNullable(response)
                .map(VoyageAIEmbeddingResponse::getData)
                .filter(data -> !data.isEmpty())
                .map(data -> data.getFirst().getEmbedding())
                .orElseThrow(() -> new RestClientServerException("임베딩 데이터가 비어 있거나 응답이 없습니다"));
    }

    private String summarizeInput(String text) {
        int maxLen = 50;
        if (text == null) return "null";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + "...(" + text.length() + " chars)";
    }
}
