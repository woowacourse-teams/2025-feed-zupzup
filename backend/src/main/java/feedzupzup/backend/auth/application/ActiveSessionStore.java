package feedzupzup.backend.auth.application;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ActiveSessionStore {

    private final Set<Long> activeSessions = ConcurrentHashMap.newKeySet();

    public void addActiveSession(Long adminId) {
        activeSessions.add(adminId);
    }

    public boolean hasActiveSession(Long adminId) {
        return activeSessions.contains(adminId);
    }

    public void removeActiveSession(Long adminId) {
        activeSessions.remove(adminId);
    }
}
