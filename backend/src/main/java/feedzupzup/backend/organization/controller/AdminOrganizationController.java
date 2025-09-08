package feedzupzup.backend.organization.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.api.AdminOrganizationApi;
import feedzupzup.backend.organization.application.AdminOrganizationService;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminInquireOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminUpdateOrganizationResponse;
import feedzupzup.backend.organizer.dto.LoginOrganizerInfo;
import java.util.List;
import java.util.UUID;
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
    public SuccessResponse<AdminUpdateOrganizationResponse> updateOrganization(
            final UUID organizationUuid,
            final LoginOrganizerInfo loginOrganizerInfo,
            final UpdateOrganizationRequest request
    ) {
        final AdminUpdateOrganizationResponse response = adminOrganizationService.updateOrganization(
                loginOrganizerInfo.organizationUuid(),
                request
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<List<AdminInquireOrganizationResponse>> getOrganizations(
            final AdminSession adminSession
    ) {
        final List<AdminInquireOrganizationResponse> response =
                adminOrganizationService.getOrganizationsInfo(adminSession.adminId());
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<Void> deleteOrganization(
            final LoginOrganizerInfo loginOrganizerInfo
    ) {
        adminOrganizationService.deleteOrganization(loginOrganizerInfo.organizationUuid());
        return SuccessResponse.success(HttpStatus.NO_CONTENT);
    }
}
