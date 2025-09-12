package feedzupzup.backend.organization.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.auth.presentation.annotation.LoginOrganizer;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminInquireOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminUpdateOrganizationResponse;
import feedzupzup.backend.organizer.dto.LoginOrganizerInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Admin Organization", description = "관리자 단체 API")
@SecurityRequirement(name = "SessionAuth")
public interface AdminOrganizationApi {

    @Operation(summary = "단체 저장", description = "단체을 저장할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "저장 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/organizations")
    SuccessResponse<AdminCreateOrganizationResponse> createOrganization(
            @Parameter(hidden = true) @AdminAuthenticationPrincipal final AdminSession adminSession,
            @RequestBody final CreateOrganizationRequest createOrganizationRequest
    );

    @Operation(summary = "단체 수정", description = "단체을 수정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "수정 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/admin/organizations/{organizationUuid}")
    SuccessResponse<AdminUpdateOrganizationResponse> updateOrganization(
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("organizationUuid") final UUID organizationUuid,
            @Parameter(hidden = true) @LoginOrganizer final LoginOrganizerInfo loginOrganizerInfo,
            @RequestBody final UpdateOrganizationRequest updateOrganizationRequest
    );

    @Operation(summary = "단체 조회", description = "본인이 속한 단체을 모두 조회할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/organizations")
    SuccessResponse<List<AdminInquireOrganizationResponse>> getOrganizations(
            @Parameter(hidden = true) @AdminAuthenticationPrincipal final AdminSession adminSession
    );

    @Operation(summary = "단체 삭제", description = "단체를 삭제할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공", useReturnTypeSchema = true)
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/organizations/{organizationUuid}")
    SuccessResponse<Void> deleteOrganization(
            @Parameter(hidden = true) @LoginOrganizer final LoginOrganizerInfo loginOrganizerInfo
    );
}
