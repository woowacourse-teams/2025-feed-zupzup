package feedzupzup.backend.feedback.domain.service.sort;

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
public class LatestFeedbackSortStrategy implements FeedbackSortStrategy {

    private final FeedbackRepository feedbackRepository;

    @Cacheable(cacheNames = "latestFeedbacks", key = "#organizationUuId", condition = "#cursorId == null and #status == null")
    @Override
    public List<FeedbackItem> getSortedFeedbacks(final UUID organizationUuId, final ProcessStatus status, final Long cursorId,
            final Pageable pageable) {

        final List<Feedback> feedbacks = feedbackRepository.findByLatest(organizationUuId, status,
                cursorId, pageable);

        return feedbacks.stream()
                .map(FeedbackItem::from)
                .toList();
    }

    @Override
    public List<FeedbackItem> sort(final List<Feedback> feedbacks) {
        return feedbacks.stream()
                .sorted(Comparator.comparing(Feedback::getId).reversed())
                .map(FeedbackItem::from)
                .toList();
    }

    @Override
    public FeedbackSortBy getType() {
        return FeedbackSortBy.LATEST;
    }
}
