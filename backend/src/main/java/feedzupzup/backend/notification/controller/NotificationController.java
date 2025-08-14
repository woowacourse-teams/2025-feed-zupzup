package feedzupzup.backend.notification.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.notification.api.NotificationApi;
import feedzupzup.backend.notification.application.NotificationService;
import feedzupzup.backend.notification.dto.AlertsSettingResponse;
import feedzupzup.backend.notification.dto.NotificationTokenRequest;
import feedzupzup.backend.notification.dto.UpdateAlertsSettingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;

    @Override
    public SuccessResponse<Void> registerNotificationToken(
            final NotificationTokenRequest request,
            final AdminSession adminSession
    ) {
        Long adminId = adminSession.adminId();
        notificationService.registerToken(request, adminId);
        return SuccessResponse.success(HttpStatus.CREATED);
    }

    @Override
    public SuccessResponse<AlertsSettingResponse> getAlertsSetting(
            final AdminSession adminSession
    ) {
        Long adminId = adminSession.adminId();
        AlertsSettingResponse response = notificationService.getAlertsSetting(adminId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<Void> updateAlertsSetting(
            final UpdateAlertsSettingRequest request,
            final AdminSession adminSession
    ) {
        Long adminId = adminSession.adminId();
        notificationService.updateAlertsSetting(request, adminId);
        return SuccessResponse.success(HttpStatus.OK);
    }
}
