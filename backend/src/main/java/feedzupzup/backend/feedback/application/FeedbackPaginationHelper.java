package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.Feedback;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FeedbackPaginationHelper {

    public PaginationData processFeedbackPagination(final List<Feedback> feedbacks, final int size) {
        final boolean hasNext = feedbacks.size() > size;
        trimExtraFeedback(feedbacks, hasNext);
        final Long nextCursorId = calculateNextCursorId(feedbacks);
        
        return new PaginationData(feedbacks, hasNext, nextCursorId);
    }

    private void trimExtraFeedback(final List<Feedback> feedbacks, final boolean hasNext) {
        if (hasNext) {
            feedbacks.removeLast();
        }
    }

    private Long calculateNextCursorId(final List<Feedback> feedbacks) {
        if (feedbacks.isEmpty()) {
            return null;
        }
        return feedbacks.getLast().getId();
    }

    public record PaginationData(
            List<Feedback> feedbacks,
            boolean hasNext,
            Long nextCursorId
    ) {
    }
}
