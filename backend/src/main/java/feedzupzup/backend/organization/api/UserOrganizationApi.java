package feedzupzup.backend.organization.api;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.dto.response.CheeringResponse;
import feedzupzup.backend.organization.dto.response.UserOrganizationResponse;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Organization", description = "단체 API")
public interface UserOrganizationApi {

    @Operation(summary = "단체를 조회", description = "단체 ID로 단체 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/organizations/{organizationId}")
    SuccessResponse<UserOrganizationResponse> getOrganizationById(
            @Parameter(description = "단체 ID", example = "1")
            @PathVariable("organizationId") final Long organizationId
    );

    @Operation(summary = "응원하기", description = "사용자는 단체를 응원할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "응원하기 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/organizations/{organizationId}/cheer")
    SuccessResponse<CheeringResponse> cheerByOrganizationId(
            @Parameter(description = "단체 ID", example = "1")
            @PathVariable("organizationId") final Long organizationId,
            @RequestBody final CheeringRequest request
    );
}
