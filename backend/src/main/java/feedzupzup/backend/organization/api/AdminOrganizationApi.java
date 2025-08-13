package feedzupzup.backend.organization.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface AdminOrganizationApi {

    @Operation(summary = "조직 저장", description = "조직을 저장할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "저장 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/organizations")
    SuccessResponse<Void> createOrganization(
            @AdminAuthenticationPrincipal final AdminSession adminSession,
            @RequestBody final CreateOrganizationRequest createOrganizationRequest
    );

}
