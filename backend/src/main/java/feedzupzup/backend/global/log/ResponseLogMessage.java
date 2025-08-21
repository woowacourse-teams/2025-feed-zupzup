package feedzupzup.backend.global.log;

import java.nio.charset.StandardCharsets;
import org.springframework.web.util.ContentCachingResponseWrapper;

public record ResponseLogMessage(
        int httpStatus,
        String responseBody
) {

    public static ResponseLogMessage createInstance(
            final ContentCachingResponseWrapper responseWrapper
    ) {
        return new ResponseLogMessage(
                responseWrapper.getStatus(),
                getResponseBody(responseWrapper)
        );
    }

    private static String getResponseBody(final ContentCachingResponseWrapper response) {
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }
}
