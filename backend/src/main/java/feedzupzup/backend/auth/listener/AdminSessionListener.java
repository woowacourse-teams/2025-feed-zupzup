package feedzupzup.backend.auth.listener;

import feedzupzup.backend.auth.application.ActiveSessionStore;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@WebListener
@Slf4j
public class AdminSessionListener implements HttpSessionListener {

    private final ActiveSessionStore activeSessionStore;
    
    @Value("${admin.session.key}")
    private String adminIdSessionKey;
    
    public AdminSessionListener(ActiveSessionStore activeSessionStore) {
        this.activeSessionStore = activeSessionStore;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Long adminId = (Long) se.getSession().getAttribute(adminIdSessionKey);
        if (adminId != null) {
            activeSessionStore.removeActiveSession(adminId);
            log.debug("Session destroyed for adminId: {}", adminId);
        }
    }
}
