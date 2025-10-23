package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.guest.domain.write.WriteHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "내 피드백 목록 응답")
public record MyFeedbackListResponse(
        @Schema(description = "피드백 목록")
        List<UserFeedbackItem> feedbacks
) {

    public static MyFeedbackListResponse from(final List<FeedbackItem> feedbackItems) {
        return new MyFeedbackListResponse(feedbackItems.stream()
                .map(UserFeedbackItem::from)
                .toList());
    }

    public static MyFeedbackListResponse fromHistory(final List<WriteHistory> writeHistories) {
        return new MyFeedbackListResponse(writeHistories.stream()
                .map(writeHistory -> FeedbackItem.from(writeHistory.getFeedback()))
                .map(UserFeedbackItem::from)
                .toList());
    }
}
