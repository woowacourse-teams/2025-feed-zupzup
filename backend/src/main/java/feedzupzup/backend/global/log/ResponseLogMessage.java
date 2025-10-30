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
        final String contentType = response.getContentType();

        // 바이너리 파일 응답인 경우 본문 로그 생략
        if (contentType != null && isBinaryContentType(contentType)) {
            return "[Binary content - " + contentType + "]";
        }

        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    private static boolean isBinaryContentType(final String contentType) {
        return contentType.contains("application/octet-stream")
                || contentType.contains("application/pdf")
                || contentType.contains("application/zip")
                || contentType.contains("image/")
                || contentType.contains("video/")
                || contentType.contains("audio/")
                || contentType.contains("application/vnd.openxmlformats-officedocument");
    }
}
