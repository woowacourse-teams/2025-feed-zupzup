package feedzupzup.backend.auth.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
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
