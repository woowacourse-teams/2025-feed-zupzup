package feedzupzup.backend.notification.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.notification.dto.NotificationTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "알람", description = "관리자 알람 API")
public interface NotificationApi {

    @Operation(summary = "알람 토큰 등록", description = "사용자의 알람 토큰 등록 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알람 토큰 등록 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    SuccessResponse<Void> registerNotificationToken(
            @RequestBody @Valid final NotificationTokenRequest request,
            @AdminAuthenticationPrincipal final AdminSession adminSession
    );
}
