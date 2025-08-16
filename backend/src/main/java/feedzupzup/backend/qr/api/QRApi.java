package feedzupzup.backend.qr.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.qr.dto.QRResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface QRApi {

    @GetMapping("/admin/organizations/{organizationUuid}/qr-code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    SuccessResponse<QRResponse> getQR(
            @AdminAuthenticationPrincipal final AdminSession adminSession,
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("organizationUuid") final UUID organizationUuid
    );
}
