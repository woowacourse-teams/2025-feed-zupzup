package feedzupzup.backend.global.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final ContentCachingRequestWrapper cacheRequest = new ContentCachingRequestWrapper(request);
        final ContentCachingResponseWrapper cacheResponse = new ContentCachingResponseWrapper(response);

        String requestId = createRequestId();

        writeRequestLog(cacheRequest);
        filterChain.doFilter(cacheRequest, cacheResponse);
        writeResponseLog(cacheResponse);

        cacheResponse.copyBodyToResponse();
        MDC.remove(requestId);
    }

    private String createRequestId() {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("request_id", requestId);
        return requestId;
    }

    private void writeRequestLog(final ContentCachingRequestWrapper cacheRequest) {
        final RequestLogMessage requestLog = RequestLogMessage.createInstance(cacheRequest);
        log.info(requestLog.toString());
    }

    private void writeResponseLog(final ContentCachingResponseWrapper cacheResponse) {
        final ResponseLogMessage responseLog = ResponseLogMessage.createInstance(cacheResponse);
        log.info(responseLog.toString());
    }
}
