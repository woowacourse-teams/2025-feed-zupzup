package feedzupzup.backend.feedback.infrastructure.ai;

import feedzupzup.backend.global.exception.InfrastructureException.RestClientServerException;
import java.util.List;
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
public class OpenAICompletionClient {

    private final OpenAIProperties openAIProperties;
    private final RestClient openAiCompletionRestClient;
    private final OpenAIErrorHandler openAIErrorHandler;

    public String generateCompletion(final String prompt, final String systemMessage) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", openAIProperties.getCompletionModel(),
                    "messages", List.of(
                            Map.of("role", "system", "content", systemMessage),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "max_tokens", openAIProperties.getMaxTokens(),
                    "temperature", openAIProperties.getTemperature()
            );

            log.info("[OpenAI] 텍스트 생성 요청 시작 - model: {}, promptLength: {}",
                    openAIProperties.getCompletionModel(),
                    prompt.length());

            OpenAICompletionResponse response = openAiCompletionRestClient.post()
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, openAIErrorHandler::handleError)
                    .body(OpenAICompletionResponse.class);

            String completion = extractCompletionOrThrow(response);

            log.info("[OpenAI] 텍스트 생성 성공 - completionLength: {}, totalTokens: {}",
                    completion.length(),
                    response.getUsage().getTotalTokens());

            return completion;
        } catch (Exception e) {
            log.error("피드백 라벨 생성 실패: {}", e.getMessage(), e);
            throw new RestClientServerException("피드백 라벨 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private String extractCompletionOrThrow(final OpenAICompletionResponse response) {
        return Optional.ofNullable(response)
                .map(OpenAICompletionResponse::getChoices)
                .filter(choices -> !choices.isEmpty())
                .map(choices -> choices.getFirst().getMessage().getContent())
                .filter(content -> !content.trim().isEmpty())
                .orElseThrow(() -> new RestClientServerException("생성된 텍스트가 비어 있거나 응답이 없습니다"));
    }
}
