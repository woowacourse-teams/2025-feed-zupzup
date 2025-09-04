package feedzupzup.backend.admin.application;

import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.notification.application.NotificationService;
import feedzupzup.backend.organizer.application.OrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final NotificationService notificationService;
    private final OrganizerService organizerService;

    @Transactional
    public void withdraw(final Long adminId) {
        notificationService.deleteAllByAdminId(adminId);
        organizerService.deleteAllByAdminId(adminId);
        adminRepository.deleteById(adminId);
    }
}
