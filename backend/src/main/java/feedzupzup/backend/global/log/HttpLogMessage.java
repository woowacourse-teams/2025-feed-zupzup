package feedzupzup.backend.global.log;

import java.nio.charset.StandardCharsets;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public record HttpLogMessage(
        String httpMethod,
        String requestUri,
        int httpStatus,
        String clientIp,
        String requestBody,
        String responseBody
) {

    public static HttpLogMessage createInstance(
            final ContentCachingRequestWrapper requestWrapper,
            final ContentCachingResponseWrapper responseWrapper
    ) {
        return new HttpLogMessage(
                requestWrapper.getMethod(),
                requestWrapper.getRequestURI(),
                responseWrapper.getStatus(),
                requestWrapper.getRemoteAddr(),
                getRequestBody(requestWrapper),
                getResponseBody(responseWrapper)
        );
    }

    private static String getRequestBody(final ContentCachingRequestWrapper request) {
        return new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    private static String getResponseBody(final ContentCachingResponseWrapper response) {
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }
}
