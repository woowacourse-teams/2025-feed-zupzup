package feedzupzup.backend.organization.application;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.FeedbackAmount;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import feedzupzup.backend.organization.dto.response.OrganizationStatisticResponse;
import java.util.List;
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

    public OrganizationStatisticResponse getStatistic(final UUID organizationUuid) {
        final Organization organization = findOrganizationBy(organizationUuid);
        final FeedbackAmount feedbackAmount = organizationStatisticRepository.findFeedbackAmountByOrganizationId(organization.getId());
        return OrganizationStatisticResponse.of(feedbackAmount);
    }

    @Transactional
    public void deleteAllByOrganizationIds(final List<Long> organizationIds) {
        organizationStatisticRepository.deleteAllByOrganizationIdIn(organizationIds);
    }

    private Organization findOrganizationBy(final UUID organizationUuid) {
        return organizationRepository.findByUuid(organizationUuid)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
    }

}
