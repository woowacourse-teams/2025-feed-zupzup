package feedzupzup.backend.notification.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "알림 토큰 등록 요청")
public record NotificationRequest(

        @Schema(description = "FCM 토큰", example = "eXAMPLE123:APA91bF...")
        @NotBlank(message = "알림 토큰은 필수입니다.")
        @JsonProperty("notificationToken")
        String token
) {

    public Notification toNotification(final Admin admin) {
        String cleanToken = token.replace("\"", "");
        return new Notification(admin, cleanToken);
    }
}
