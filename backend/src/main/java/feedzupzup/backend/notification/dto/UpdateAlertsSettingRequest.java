package feedzupzup.backend.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "알람 설정 변경 요청")
public record UpdateAlertsSettingRequest(
        
        @Schema(description = "알람 설정 (true: 알람 받음, false: 알람 받지 않음)", example = "true")
        @NotNull(message = "알람 설정은 필수입니다.")
        Boolean alertsOn
) {
}