package feedzupzup.backend.global.log;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import io.opentelemetry.api.trace.Span;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDE_URI = List.of(
            "/actuator/**",
            "/swagger-ui/**",
            "/api-docs"
    );

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        if (isExcluded(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        final ContentCachingRequestWrapper cacheRequest = new ContentCachingRequestWrapper(request);
        final ContentCachingResponseWrapper cacheResponse = new ContentCachingResponseWrapper(response);

        createTraceId();
        createSpanId();

        writeRequestLog(cacheRequest);
        filterChain.doFilter(cacheRequest, cacheResponse);
        writeResponseLog(cacheResponse);

        cacheResponse.copyBodyToResponse();
        MDC.remove("trace_id");
        MDC.remove("span_id");
        MDC.remove("global_trace_id");
    }

    private void createTraceId() {
        final String traceId = Span.current().getSpanContext().getTraceId();
        if (!traceId.isEmpty()) {
            MDC.put("trace_id", traceId);
        } else {
            MDC.put("trace_id", UUID.randomUUID().toString());
        }
    }

    private void createSpanId() {
        final String spanId = Span.current().getSpanContext().getSpanId();
        if (!spanId.isEmpty()) {
            MDC.put("span_id", spanId);
        } else {
            MDC.put("span_id", UUID.randomUUID().toString());
        }
    }

    private void writeRequestLog(final ContentCachingRequestWrapper cacheRequest) {
        final RequestLogMessage requestLog = RequestLogMessage.createInstance(cacheRequest);
        log.info("HTTP Request",
                keyValue("httpMethod", requestLog.httpMethod()),
                keyValue("requestUri", requestLog.requestUri()),
                keyValue("requestParam", requestLog.requestParam()),
                keyValue("clientIp", requestLog.clientIp()),
                keyValue("requestBody", requestLog.requestBody())
        );
    }

    private void writeResponseLog(final ContentCachingResponseWrapper cacheResponse) {
        final ResponseLogMessage responseLog = ResponseLogMessage.createInstance(cacheResponse);
        log.info("HTTP Response",
                keyValue("httpStatus", responseLog.httpStatus()),
                keyValue("responseBody", responseLog.responseBody())
        );
    }

    private boolean isExcluded(String requestURI) {
        return EXCLUDE_URI.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, requestURI));
    }
}
