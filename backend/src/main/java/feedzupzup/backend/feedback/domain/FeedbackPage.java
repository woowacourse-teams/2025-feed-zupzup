package feedzupzup.backend.feedback.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class FeedbackPage {

    private final List<Feedback> feedbacks;
    private final boolean hasNext;

    public FeedbackPage(final List<Feedback> feedbacks, final int size) {
        this.hasNext = feedbacks.size() > size;
        trimExtraFeedback(feedbacks);
        this.feedbacks = feedbacks;
    }

    public Long calculateNextCursorId() {
        if (feedbacks.isEmpty()) {
            return null;
        }
        return feedbacks.getLast().getId();
    }

    private void trimExtraFeedback(final List<Feedback> feedbacks) {
        if (hasNext) {
            feedbacks.removeLast();
        }
    }
}
