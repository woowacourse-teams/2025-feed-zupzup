package feedzupzup.backend.organization.application;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
import feedzupzup.backend.organization.dto.response.CheeringResponse;
import feedzupzup.backend.organization.dto.response.UserOrganizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserOrganizationService {

    private final OrganizationRepository organizationRepository;

    public UserOrganizationResponse getOrganizationById(final Long organizationId) {
        final Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + organizationId + ")인 단체를 찾을 수 없습니다."));

        return UserOrganizationResponse.from(organization);
    }

    @Transactional
    public CheeringResponse cheer(final CheeringRequest request, final Long organizationId) {
        final Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + organizationId + ")인 단체를 찾을 수 없습니다."));

        organization.cheer(request.toCheeringCount());

        return CheeringResponse.from(organization.getCheeringCount());
    }
}
