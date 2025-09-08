package feedzupzup.backend.notification.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.notification.domain.NotificationToken;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "알림 토큰 등록 요청")
public record NotificationTokenRequest(
        
        @Schema(description = "FCM 토큰", example = "eXAMPLE123:APA91bF...")
        @NotBlank(message = "알림 토큰은 필수입니다.")
        @JsonProperty("notificationToken")
        String value
) {

    public NotificationToken toNotificationToken(final Admin admin) {
        String cleanToken = value.replace("\"", "");
        return new NotificationToken(admin, cleanToken);
    }
}
