package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "내 피드백 목록 응답")
public record MyFeedbackListResponse(
        @Schema(description = "피드백 목록")
        List<FeedbackItem> feedbacks
) {

    public static MyFeedbackListResponse of(final List<Feedback> feedbacks) {
        final List<FeedbackItem> myFeedbackItems = feedbacks.stream()
                .map(FeedbackItem::from)
                .toList();
        return new MyFeedbackListResponse(myFeedbackItems);
    }
}
