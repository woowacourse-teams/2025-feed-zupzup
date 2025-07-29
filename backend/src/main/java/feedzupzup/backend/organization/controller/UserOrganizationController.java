package feedzupzup.backend.organization.controller;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.api.UserOrganizationApi;
import feedzupzup.backend.organization.application.UserOrganizationService;
import feedzupzup.backend.organization.dto.UserOrganizationResponse;
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
}
