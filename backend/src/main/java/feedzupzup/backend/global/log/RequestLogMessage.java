package feedzupzup.backend.global.log;

import java.nio.charset.StandardCharsets;
import org.springframework.web.util.ContentCachingRequestWrapper;

public record RequestLogMessage(
        String httpMethod,
        String requestUri,
        String clientIp,
        String requestBody
) {
    public static RequestLogMessage createInstance(
            final ContentCachingRequestWrapper requestWrapper
    ) {
        return new RequestLogMessage(
                requestWrapper.getMethod(),
                requestWrapper.getRequestURI(),
                requestWrapper.getRemoteAddr(),
                getRequestBody(requestWrapper)
        );
    }

    private static String getRequestBody(final ContentCachingRequestWrapper request) {
        return new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
    }
}
