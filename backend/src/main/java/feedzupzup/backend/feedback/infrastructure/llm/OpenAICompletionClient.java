package feedzupzup.backend.feedback.infrastructure.llm;

import feedzupzup.backend.global.async.exception.NonRetryableException;
import feedzupzup.backend.global.async.exception.RetryableException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenAICompletionClient {

    private final OpenAIProperties openAIProperties;
    private final RestClient openAiCompletionRestClient;
    private final OpenAIErrorHandler openAIErrorHandler;

    @Retryable(
            retryFor = {RetryableException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public String generateCompletion(final String prompt, final String systemMessage) {
        final Map<String, Object> requestBody = Map.of(
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

        final OpenAICompletionResponse response = openAiCompletionRestClient.post()
                .body(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, openAIErrorHandler::handleError)
                .body(OpenAICompletionResponse.class);

        final String completion = extractCompletionOrThrow(response);

        log.info("[OpenAI] 텍스트 생성 성공 - completionLength: {}, totalTokens: {}",
                completion.length(),
                response.getUsage().getTotalTokens());

        return completion;
    }

    private String extractCompletionOrThrow(final OpenAICompletionResponse response) {
        return Optional.ofNullable(response)
                .map(OpenAICompletionResponse::getChoices)
                .filter(choices -> !choices.isEmpty())
                .map(choices -> choices.getFirst().getMessage().getContent())
                .filter(content -> !content.trim().isEmpty())
                .orElseThrow(() -> new RetryableException("생성된 텍스트가 비어 있거나 응답이 없습니다"));
    }
}
