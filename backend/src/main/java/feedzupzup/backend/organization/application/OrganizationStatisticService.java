package feedzupzup.backend.organization.application;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.domain.OrganizationStatistic;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import feedzupzup.backend.organization.dto.response.OrganizationStatisticResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationStatisticService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationStatisticRepository organizationStatisticRepository;

    public OrganizationStatisticResponse calculateStatistic(final UUID organizationUuid) {
        final Organization organization = findOrganizationBy(organizationUuid);

        final OrganizationStatistic organizationStatistic = organizationStatisticRepository.findByOrganizationId(
                organization.getId());

        return OrganizationStatisticResponse.of(organizationStatistic.getFeedbackAmount());
    }

    private Organization findOrganizationBy(final UUID organizationUuid) {
        return organizationRepository.findByUuid(organizationUuid)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
    }

}
