package feedzupzup.backend.organization.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.api.AdminOrganizationApi;
import feedzupzup.backend.organization.application.AdminOrganizationService;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminOrganizationController implements AdminOrganizationApi {

    private final AdminOrganizationService adminOrganizationService;

    @Override
    public SuccessResponse<Void> createOrganization(
            @AdminAuthenticationPrincipal final AdminSession adminSession,
            final CreateOrganizationRequest request
    ) {
        adminOrganizationService.saveOrganization(request, adminSession.adminId());
        return SuccessResponse.success(HttpStatus.CREATED);
    }
}
