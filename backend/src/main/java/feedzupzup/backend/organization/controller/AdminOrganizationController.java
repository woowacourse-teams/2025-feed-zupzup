package feedzupzup.backend.organization.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.api.AdminOrganizationApi;
import feedzupzup.backend.organization.application.AdminOrganizationService;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminInquireOrganizationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminOrganizationController implements AdminOrganizationApi {

    private final AdminOrganizationService adminOrganizationService;

    @Override
    public SuccessResponse<AdminCreateOrganizationResponse> createOrganization(
            @AdminAuthenticationPrincipal final AdminSession adminSession,
            final CreateOrganizationRequest request
    ) {
        final AdminCreateOrganizationResponse response = adminOrganizationService.createOrganization(
                request, adminSession.adminId());
        return SuccessResponse.success(HttpStatus.CREATED, response);
    }

    @Override
    public SuccessResponse<List<AdminInquireOrganizationResponse>> getOrganizations(
            final AdminSession adminSession
    ) {
        final List<AdminInquireOrganizationResponse> response =
                adminOrganizationService.getOrganizationsInfo(adminSession.adminId());
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
