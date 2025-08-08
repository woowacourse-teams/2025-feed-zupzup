package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedbackAmount;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.dto.response.StatisticResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedbackStatisticService {

    private final FeedbackRepository feedbackRepository;
    private final OrganizationRepository organizationRepository;

    public StatisticResponse calculateStatistic(final Long organizationId) {
        final Organization organization = findOrganizationBy(organizationId);
        final FeedbackAmount feedbackAmount = feedbackRepository.countFeedbackByOrganizationIdAndProcessStatus(organization.getId());
        return StatisticResponse.of(feedbackAmount, feedbackAmount.calculateReflectionRate());
    }

    private Organization findOrganizationBy(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
    }
}
