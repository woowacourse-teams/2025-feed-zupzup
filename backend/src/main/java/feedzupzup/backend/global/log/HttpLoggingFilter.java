package feedzupzup.backend.global.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
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
        filterChain.doFilter(cacheRequest, cacheResponse);
        generateLogging(cacheRequest, cacheResponse);
    }

    private void generateLogging(
            final ContentCachingRequestWrapper cacheRequest,
            final ContentCachingResponseWrapper cacheResponse
    ) throws IOException {
        final HttpLogMessage logMessage = HttpLogMessage.createInstance(cacheRequest, cacheResponse);
        log.info(logMessage.toString());
        cacheResponse.copyBodyToResponse();
    }
}
