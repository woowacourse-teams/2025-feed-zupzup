package feedzupzup.backend.feedback.infrastructure.embedding;

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
public class VoyageAIErrorHandler {

    private final ObjectMapper objectMapper;

    public void handleError(final HttpRequest request, final ClientHttpResponse response) {
        try {
            final HttpStatus httpStatus = HttpStatus.valueOf(response.getStatusCode().value());
            try (InputStream bodyStream = response.getBody()) {
                final VoyageAIErrorResponse errorResponse = objectMapper.readValue(bodyStream, VoyageAIErrorResponse.class);
                log.warn("VoyageAI API 오류 [{}] : {}", httpStatus, errorResponse.getDetail());

                if (isRetryableError(httpStatus)) {
                    throw new RetryableException(String.format("VoyageAI API 일시적 오류 [%s]: %s",
                            httpStatus, errorResponse.getDetail()));
                }
                throw new NonRetryableException(String.format("VoyageAI API 오류 [%s]: %s",
                        httpStatus, errorResponse.getDetail()));
            }
        } catch (IOException e) {
            log.error("VoyageAI Error Parsing Failed: {}", e.getMessage());
            throw new RetryableException("VoyageAI 에러 응답 파싱 실패", e);
        }
    }

    private boolean isRetryableError(final HttpStatus httpStatus) {
        return httpStatus == HttpStatus.TOO_MANY_REQUESTS ||
                httpStatus == HttpStatus.REQUEST_TIMEOUT ||
                httpStatus == HttpStatus.SERVICE_UNAVAILABLE ||
                httpStatus == HttpStatus.BAD_GATEWAY ||
                httpStatus == HttpStatus.GATEWAY_TIMEOUT ||
                httpStatus.is5xxServerError();
    }
}
