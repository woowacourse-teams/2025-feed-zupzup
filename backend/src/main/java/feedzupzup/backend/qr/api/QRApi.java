package feedzupzup.backend.qr.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.qr.dto.response.QRDownloadUrlResponse;
import feedzupzup.backend.qr.dto.response.QRResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "QR", description = "관리자 QR API")
@SecurityRequirement(name = "SessionAuth")
public interface QRApi {

    @Operation(summary = "QR 조회", description = "QR 조회 (관리자 전용)")
    @GetMapping("/admin/organizations/{organizationUuid}/qr-code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    SuccessResponse<QRResponse> getQR(
            @Parameter(hidden = true)
            @AdminAuthenticationPrincipal final AdminSession adminSession,
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("organizationUuid") final UUID organizationUuid
    );

    @Operation(summary = "QR 다운로드 URL 조회", description = "QR 다운로드 URL 조회 (관리자 전용)")
    @GetMapping("/admin/organizations/{organizationUuid}/qr-code/download-url")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    SuccessResponse<QRDownloadUrlResponse> getQRDownloadUrl(
            @Parameter(hidden = true)
            @AdminAuthenticationPrincipal final AdminSession adminSession,
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("organizationUuid") final UUID organizationUuid
    );
    
}
