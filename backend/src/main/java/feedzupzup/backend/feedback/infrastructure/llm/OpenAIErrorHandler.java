package feedzupzup.backend.feedback.infrastructure.llm;

import static feedzupzup.backend.feedback.infrastructure.llm.OpenAIErrorResponse.ErrorBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.global.async.exception.NonRetryableException;
import feedzupzup.backend.global.async.exception.RetryableException;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class OpenAIErrorHandler {

    private final ObjectMapper objectMapper;

    public void handleError(final HttpRequest request, final ClientHttpResponse response) {
        try {
            final HttpStatus httpStatus = HttpStatus.valueOf(response.getStatusCode().value());

            try (InputStream bodyStream = response.getBody()) {
                final OpenAIErrorResponse errorResponse = objectMapper.readValue(bodyStream, OpenAIErrorResponse.class);
                final ErrorBody errorBody = errorResponse.getError();
                log.warn("OpenAI API 오류 [{}:{}] {}", errorBody.getType(), errorBody.getCode(),
                        errorBody.getMessage());

                if (isRetryableError(httpStatus, errorBody)) {
                    throw new RetryableException(
                            String.format("OpenAI API 일시적 오류 [%s]: %s", httpStatus, errorBody.getMessage()));
                }
                throw new NonRetryableException(
                        String.format("OpenAI API 오류 [%s]: %s", httpStatus, errorBody.getMessage()));
            }
        } catch (IOException e) {
            log.error("OpenAI Error Parsing Failed: {}", e.getMessage());
            throw new RetryableException("OpenAI 에러 응답 파싱 실패", e);
        }
    }

    private boolean isRetryableError(final HttpStatus httpStatus, final ErrorBody errorBody) {
        if (
            httpStatus == HttpStatus.TOO_MANY_REQUESTS ||
            httpStatus == HttpStatus.REQUEST_TIMEOUT ||
            httpStatus == HttpStatus.SERVICE_UNAVAILABLE ||
            httpStatus == HttpStatus.BAD_GATEWAY ||
            httpStatus == HttpStatus.GATEWAY_TIMEOUT ||
            httpStatus.is5xxServerError()
        ) {
            return true;
        }
        if (errorBody == null || errorBody.getType() == null) {
            return false;
        }

        String errorType = errorBody.getType().toLowerCase();
        return errorType.contains("server_error") ||
                errorType.contains("rate_limit") ||
                errorType.contains("timeout") ||
                errorType.contains("insufficient_quota");
    }
}
