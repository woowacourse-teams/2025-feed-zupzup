package feedzupzup.backend.auth.listener;

import feedzupzup.backend.auth.application.ActiveSessionStore;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class AdminSessionListener implements HttpSessionListener {

    private final ActiveSessionStore activeSessionStore;
    private final String adminIdSessionKey;

    public AdminSessionListener(
            final ActiveSessionStore activeSessionStore,
            @Value("${admin.session.key}") final String adminIdSessionKey) {
        this.activeSessionStore = activeSessionStore;
        this.adminIdSessionKey = adminIdSessionKey;
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent session) {
        Long adminId = (Long) session.getSession().getAttribute(adminIdSessionKey);
        if (adminId != null) {
            activeSessionStore.removeActiveSession(adminId);
            log.info("활성 세션에서 관리자 id = {} 삭제", adminId);
        }
    }
}
