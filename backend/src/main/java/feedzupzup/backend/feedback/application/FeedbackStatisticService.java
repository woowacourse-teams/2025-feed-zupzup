package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedbacks;
import feedzupzup.backend.feedback.dto.response.StatisticResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedbackStatisticService {

    private final FeedBackRepository feedBackRepository;
    private final OrganizationRepository organizationRepository;

    public StatisticResponse calculateStatistic(final Long organizationId, final int period) {
        final Organization organization = findOrganizationBy(organizationId);
        final int targetPeriod = period - 1;
        final LocalDateTime targetDateTime = LocalDate.now(ZoneId.of("Asia/Seoul"))
                .minusDays(targetPeriod).atStartOfDay();
        final Feedbacks feedbacks = new Feedbacks(
                feedBackRepository.findByOrganizationIdAndPostedAtAfter(
                        organization.getId(),
                        targetDateTime
                )
        );
        return StatisticResponse.of(feedbacks);
    }

    private Organization findOrganizationBy(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
    }
}
