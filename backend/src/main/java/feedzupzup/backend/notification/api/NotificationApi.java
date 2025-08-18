package feedzupzup.backend.notification.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.notification.dto.response.AlertsSettingResponse;
import feedzupzup.backend.notification.dto.request.NotificationTokenRequest;
import feedzupzup.backend.notification.dto.request.UpdateAlertsSettingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Notification", description = "관리자 알람 API")
@SecurityRequirement(name = "SessionAuth")
public interface NotificationApi {

    @Operation(summary = "알람 토큰 등록", description = "사용자의 알람 토큰 등록 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "알람 토큰 등록 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/notifications/token")
    SuccessResponse<Void> registerNotificationToken(
            @RequestBody @Valid final NotificationTokenRequest request,
            @AdminAuthenticationPrincipal final AdminSession adminSession
    );

    @Operation(summary = "알람 설정 조회", description = "관리자의 알람 설정 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알람 설정 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @GetMapping("/admin/notifications/settings")
    SuccessResponse<AlertsSettingResponse> getAlertsSetting(
            @AdminAuthenticationPrincipal final AdminSession adminSession
    );

    @Operation(summary = "알람 설정 변경", description = "관리자의 알람 on/off 설정 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알람 설정 변경 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @PatchMapping("/admin/notifications/settings")
    SuccessResponse<Void> updateAlertsSetting(
            @RequestBody @Valid final UpdateAlertsSettingRequest request,
            @AdminAuthenticationPrincipal final AdminSession adminSession
    );
}
