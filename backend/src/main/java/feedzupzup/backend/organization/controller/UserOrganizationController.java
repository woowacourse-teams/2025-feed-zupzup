package feedzupzup.backend.organization.controller;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organization.api.UserOrganizationApi;
import feedzupzup.backend.organization.application.OrganizationStatisticService;
import feedzupzup.backend.organization.application.UserOrganizationService;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
import feedzupzup.backend.organization.dto.response.CheeringResponse;
import feedzupzup.backend.organization.dto.response.OrganizationStatisticResponse;
import feedzupzup.backend.organization.dto.response.UserOrganizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserOrganizationController implements UserOrganizationApi {

    private final UserOrganizationService userOrganizationService;
    private final OrganizationStatisticService organizationStatisticService;

    @Override
    public SuccessResponse<UserOrganizationResponse> getOrganizationByUuid(final UUID organizationUuid) {
        final UserOrganizationResponse response = userOrganizationService.getOrganizationByUuid(organizationUuid);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<CheeringResponse> cheerByOrganizationUuid(
            final UUID organizationUuid,
            final CheeringRequest request
    ) {
        CheeringResponse response = userOrganizationService.cheer(request, organizationUuid);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<OrganizationStatisticResponse> getStatistic(final UUID organizationUuid) {
        final OrganizationStatisticResponse response = organizationStatisticService.calculateStatistic(
                organizationUuid
        );
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
