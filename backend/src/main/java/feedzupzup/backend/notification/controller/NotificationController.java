package feedzupzup.backend.notification.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.notification.api.NotificationApi;
import feedzupzup.backend.notification.application.NotificationService;
import feedzupzup.backend.notification.dto.NotificationTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final NotificationService notificationservice;

    public SuccessResponse<Void> registerNotificationToken(
            final NotificationTokenRequest request,
            final AdminSession adminSession
    ) {
        Long adminId = adminSession.adminId();
        notificationservice.registerToken(request, adminId);
        return SuccessResponse.success(HttpStatus.CREATED);
    }
}
