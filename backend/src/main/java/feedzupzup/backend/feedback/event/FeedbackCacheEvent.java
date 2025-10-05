package feedzupzup.backend.feedback.event;

import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.UUID;

public record FeedbackCacheEvent(
        FeedbackItem feedbackItem,
        UUID key,
        FeedbackSortType type
) {

}
