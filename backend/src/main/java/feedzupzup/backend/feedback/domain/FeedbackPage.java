package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class FeedbackPage {

    private final List<FeedbackItem> feedbackItems;
    private final boolean hasNext;

    private FeedbackPage(final List<FeedbackItem> feedbackItems, final int size) {
        this.hasNext = feedbackItems.size() > size;
        this.feedbackItems = trimExtraFeedback(feedbackItems);
    }

    public static FeedbackPage createCursorPage(final List<FeedbackItem> feedbackItems, final int size) {
        return new FeedbackPage(feedbackItems, size);
    }

    public Long calculateNextCursorId() {
        if (feedbackItems.isEmpty()) {
            return null;
        }
        return feedbackItems.getLast().feedbackId();
    }

    private List<FeedbackItem> trimExtraFeedback(final List<FeedbackItem> feedbackItems) {
        if (hasNext) {
            List<FeedbackItem> copy = new ArrayList<>(feedbackItems);
            copy.removeLast();
            return copy;
        }
        return feedbackItems;
    }
}
