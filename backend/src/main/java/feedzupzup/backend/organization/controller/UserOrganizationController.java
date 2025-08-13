package feedzupzup.backend.organization.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.api.UserOrganizationApi;
import feedzupzup.backend.organization.application.UserOrganizationService;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.CheeringResponse;
import feedzupzup.backend.organization.dto.response.UserOrganizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserOrganizationController implements UserOrganizationApi {

    private final UserOrganizationService userOrganizationService;

    @Override
    public SuccessResponse<UserOrganizationResponse> getOrganizationById(final Long organizationId) {
        final UserOrganizationResponse response = userOrganizationService.getOrganizationById(organizationId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<CheeringResponse> cheerByOrganizationId(
            final Long organizationId,
            final CheeringRequest request
    ) {
        CheeringResponse response = userOrganizationService.cheer(request, organizationId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
