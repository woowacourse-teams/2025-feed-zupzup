package feedzupzup.backend.auth.application;

import feedzupzup.backend.admin.domain.AdminRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionValidationService {

    private final AdminRepository adminRepository;
    private final ActiveSessionStore activeSessionStore;

    public List<Long> getValidSessionAdminIds(final List<Long> adminIds) {
        List<Long> activeSessionAdminIds = adminIds.stream()
                .filter(activeSessionStore::hasActiveSession)
                .toList();

        if (activeSessionAdminIds.isEmpty()) {
            return List.of();
        }

        return adminRepository.findAlertsEnabledAdminIds(activeSessionAdminIds);
    }
}
