package feedzupzup.backend.auth.presentation.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "https://feedzupzup.com",
            "https://dev.feedzupzup.com"
    );

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {

        HttpServletRequest httpRequest = request;
        HttpServletResponse httpResponse = response;

        String origin = httpRequest.getHeader("Origin");

        // Origin 검증 및 설정
        if (isOriginAllowed(origin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
        }

        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, X-Requested-With");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // OPTIONS 요청이면 200 OK로 바로 응답
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isOriginAllowed(String origin) {
        if (origin == null) {
            return false;
        }

        // 정확한 origin 매칭
        if (ALLOWED_ORIGINS.contains(origin)) {
            return true;
        }

        // 192.168.*.*:3000 패턴 매칭
        return origin.matches("^http://192\\.168\\.\\d+\\.\\d+:3000$");
    }
}
