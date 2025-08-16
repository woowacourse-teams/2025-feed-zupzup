package feedzupzup.backend.qr.api;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.qr.dto.QRResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface QRApi {

    @PostMapping("/organizations/{organizationUuid}/qr-code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    SuccessResponse<QRResponse> getQR(
            @Parameter(description = "단체 ID", example = "1") @PathVariable("organizationUuid") final String organizationUuid
    );
}
