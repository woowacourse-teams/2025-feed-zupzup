package feedzupzup.backend.feedback.domain.service;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortBy;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LatestFeedbackSortStrategy implements FeedbackSortStrategy {

    private final FeedbackRepository feedbackRepository;

    @Override
    public List<Feedback> getSortedFeedbacks(final UUID organizationUuId, final ProcessStatus status, final Long cursorId,
            final Pageable pageable) {
        return feedbackRepository.findByLatest(organizationUuId, status, cursorId, pageable);
    }

    @Override
    public List<Feedback> sort(final List<Feedback> feedbacks) {
        return feedbacks.stream()
                .sorted(Comparator.comparing(Feedback::getId).reversed())
                .toList();
    }

    @Override
    public FeedbackSortBy getType() {
        return FeedbackSortBy.LATEST;
    }
}
