package feedzupzup.backend.auth.session;

import feedzupzup.backend.admin.dto.AdminSession;
import jakarta.servlet.http.HttpServletRequest;

public interface SessionManager {
    
    AdminSession getAdminSession(HttpServletRequest request);
    
    void createAdminSession(HttpServletRequest request, Long adminId);
    
    void removeAdminSession(HttpServletRequest request);
}
