package feedzupzup.backend.auth.presentation.session;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.exception.AuthException.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpSessionManager {

    private static final int TWO_WEEKS_IN_SECONDS = 14 * 24 * 60 * 60;
    private final String adminIdSessionKey;

    public HttpSessionManager(@Value("${admin.session.key}") final String adminIdSessionKey) {
        this.adminIdSessionKey = adminIdSessionKey;
    }

    public AdminSession getAdminSession(final HttpServletRequest request) {
        final HttpSession session = getExistingSession(request);
        final Long adminId = getAdminIdFromSession(session);

        return new AdminSession(adminId);
    }

    public void createAdminSession(final HttpServletRequest request, final Long adminId) {
        final HttpSession session = request.getSession(true);
        session.setAttribute(adminIdSessionKey, adminId);
        session.setMaxInactiveInterval(TWO_WEEKS_IN_SECONDS);
    }

    public void removeAdminSession(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private HttpSession getExistingSession(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            removeAdminSession(request);
            throw new UnauthorizedException("세션을 찾을 수 없습니다");
        }
        return session;
    }

    private Long getAdminIdFromSession(final HttpSession session) {
        final Long adminId = (Long) session.getAttribute(adminIdSessionKey);
        if (adminId == null) {
            throw new UnauthorizedException("관리자가 로그인되어 있지 않습니다");
        }
        return adminId;
    }
}
