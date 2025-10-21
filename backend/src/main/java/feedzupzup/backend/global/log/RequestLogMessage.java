package feedzupzup.backend.global.log;

import java.nio.charset.StandardCharsets;
import org.springframework.web.util.ContentCachingRequestWrapper;

public record RequestLogMessage(
        String httpMethod,
        String requestUri,
        String requestParam,
        String clientIp,
        String requestBody
) {

    public static RequestLogMessage createInstance(
            final ContentCachingRequestWrapper requestWrapper
    ) {
        return new RequestLogMessage(
                requestWrapper.getMethod(),
                requestWrapper.getRequestURI(),
                requestWrapper.getQueryString(),
                parseClientIp(requestWrapper),
                getRequestBody(requestWrapper)
        );
    }

    private static String parseClientIp(final ContentCachingRequestWrapper request) {
        final String clientIp = getClientIp(request);

        if (clientIp == null || clientIp.isEmpty()) {
            return request.getRemoteAddr();
        }
        return clientIp.split(",")[0].trim();
    }

    private static String getClientIp(final ContentCachingRequestWrapper request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getHeader("X-Real-IP");
        }
        return clientIp;
    }

    private static String getRequestBody(final ContentCachingRequestWrapper request) {
        return new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
    }
}
