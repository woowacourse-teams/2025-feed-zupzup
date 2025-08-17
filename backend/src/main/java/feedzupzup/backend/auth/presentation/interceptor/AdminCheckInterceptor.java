package feedzupzup.backend.auth.presentation.interceptor;

import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.auth.exception.AuthException.UnauthorizedException;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        Long adminId = (Long) session.getAttribute("adminId");

        if (adminId == null) {
            throw new UnauthorizedException();
        }

        if (!adminRepository.existsById(adminId)) {
            throw new ForbiddenException("해당 관리자 ID(adminId = " + adminId + ")에는 권한이 없습니다.");
        }

        return true;
    }
}
