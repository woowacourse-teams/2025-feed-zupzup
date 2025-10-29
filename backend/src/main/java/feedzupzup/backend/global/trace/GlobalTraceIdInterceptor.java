package feedzupzup.backend.global.trace;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;
import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.GUEST_ID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class GlobalTraceIdInterceptor implements HandlerInterceptor {

    private static final String GLOBAL_TRACE_ID_KEY = "global_trace_id";
    private static final String ADMIN_PREFIX = "admin";
    private static final String GUEST_PREFIX = "guest";
    private static final String ANONYMOUS_PREFIX = "anon";

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        final String globalTraceId = generateGlobalTraceId(request);
        MDC.put(GLOBAL_TRACE_ID_KEY, globalTraceId);

        return true;
    }

    private String generateGlobalTraceId(final HttpServletRequest request) {
        final Long adminId = (Long) request.getAttribute(ADMIN_ID.getValue());
        if (adminId != null) {
            return String.format("%s-%d", ADMIN_PREFIX, adminId);
        }

        final UUID guestId = (UUID) request.getAttribute(GUEST_ID.getValue());
        if (guestId != null) {
            return String.format("%s-%s", GUEST_PREFIX, guestId);
        }

        return String.format("%s-%s", ANONYMOUS_PREFIX, UUID.randomUUID());
    }
}
