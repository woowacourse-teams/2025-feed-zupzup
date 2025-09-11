package feedzupzup.backend.auth.application;

import feedzupzup.backend.admin.domain.Admin;
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
        return adminIds.stream()
                .filter(this::hasValidSession)
                .filter(this::isAlertsEnabled)
                .toList();
    }

    private boolean hasValidSession(final Long adminId) {
        return activeSessionStore.hasActiveSession(adminId);
    }

    private boolean isAlertsEnabled(final Long adminId) {
        return adminRepository.findById(adminId)
                .map(Admin::isAlertsOn)
                .orElse(false);
    }
}
