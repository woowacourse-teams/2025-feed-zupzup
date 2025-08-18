package feedzupzup.backend.auth.presentation.interceptor;

import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.auth.exception.AuthException.UnauthorizedException;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminCheckInterceptor implements HandlerInterceptor {

    private final AdminRepository adminRepository;
    private final HttpSessionManager httpSessionManager;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        final AdminSession adminSession = httpSessionManager.getAdminSession(request);
        final Long adminId = adminSession.adminId();

        if (adminId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        if (!adminRepository.existsById(adminId)) {
            httpSessionManager.removeAdminSession(request);
            throw new ForbiddenException("해당 관리자 ID(adminId = " + adminId + ")에는 권한이 없습니다.");
        }

        return true;
    }
}
