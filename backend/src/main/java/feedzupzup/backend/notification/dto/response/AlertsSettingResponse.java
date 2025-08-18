package feedzupzup.backend.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알람 설정 조회 응답")
public record AlertsSettingResponse(
        
        @Schema(description = "알람 설정 (true: 알람 받음, false: 알람 받지 않음)", example = "true")
        Boolean alertsOn
) {
    
    public static AlertsSettingResponse from(boolean alertsOn) {
        return new AlertsSettingResponse(alertsOn);
    }
}
