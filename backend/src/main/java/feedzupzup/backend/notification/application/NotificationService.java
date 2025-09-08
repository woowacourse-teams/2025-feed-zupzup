package feedzupzup.backend.notification.application;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.notification.domain.NotificationToken;
import feedzupzup.backend.notification.domain.NotificationTokenRepository;
import feedzupzup.backend.notification.dto.request.NotificationTokenRequest;
import feedzupzup.backend.notification.dto.request.UpdateAlertsSettingRequest;
import feedzupzup.backend.notification.dto.response.AlertsSettingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationTokenRepository notificationTokenRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public void registerToken(final NotificationTokenRequest request, final Long adminId) {
        notificationTokenRepository.findByAdminIdAndValue(adminId, request.value())
                .ifPresentOrElse(
                        existingToken -> updateExistingToken(existingToken, request.value()),
                        () -> createNewToken(request, adminId)
                );
    }

    public AlertsSettingResponse getAlertsSetting(final Long adminId) {
        final Admin admin = getAdminById(adminId);
        return AlertsSettingResponse.from(admin.isAlertsOn());
    }

    @Transactional
    public void updateAlertsSetting(final UpdateAlertsSettingRequest request, final Long adminId) {
        final Admin admin = getAdminById(adminId);
        admin.updateAlertsSetting(request.alertsOn());
    }

    @Transactional
    public void deleteAllByAdminId(final Long adminId) {
        notificationTokenRepository.deleteAllByAdmin_Id(adminId);
    }

    private Admin getAdminById(Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("관리자 정보를 찾을 수 없습니다. ID: " + adminId));
    }

    private void updateExistingToken(final NotificationToken token, final String newToken) {
        token.updateNotificationToken(newToken);
        log.info("토큰 업데이트");
    }

    private void createNewToken(final NotificationTokenRequest request, final Long adminId) {
        final Admin admin = getAdminById(adminId);
        notificationTokenRepository.save(request.toNotificationToken(admin));
        log.info("새 토큰 등록");
    }
}
