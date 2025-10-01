package feedzupzup.backend.feedback.domain.service;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortBy;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OldestFeedbackSortStrategy implements FeedbackSortStrategy {

    private final FeedbackRepository feedbackRepository;

    @Cacheable(cacheNames = "oldestFeedbacks", key = "#organizationUuId", condition = "#cursorId == null and #status == T(feedzupzup.backend.feedback.domain.vo.ProcessStatus).WAITING")
    @Override
    public List<FeedbackItem> getSortedFeedbacks(
            final UUID organizationUuId,
            final ProcessStatus status,
            final Long cursorId,
            final Pageable pageable
    ) {
        final List<Feedback> feedbacks = feedbackRepository.findByOldest(organizationUuId, status,
                cursorId, pageable);

        return feedbacks.stream()
                .map(FeedbackItem::from)
                .toList();
    }

    @Override
    public FeedbackSortBy getType() {
        return FeedbackSortBy.OLDEST;
    }

    @Override
    public List<FeedbackItem> sort(final List<Feedback> feedbacks) {
        return feedbacks.stream()
                .sorted(Comparator.comparing(Feedback::getId))
                .map(FeedbackItem::from)
                .toList();
    }
}
