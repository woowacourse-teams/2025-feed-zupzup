package feedzupzup.backend.organization.controller;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.api.UserOrganizationApi;
import feedzupzup.backend.organization.application.UserOrganizationService;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
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
    public SuccessResponse<UserOrganizationResponse> getOrganizationByUuid(final String organizationUuid) {
        final UserOrganizationResponse response = userOrganizationService.getOrganizationByUuid(organizationUuid);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<CheeringResponse> cheerByOrganizationUuid(
            final String organizationUuid,
            final CheeringRequest request
    ) {
        CheeringResponse response = userOrganizationService.cheer(request, organizationUuid);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
