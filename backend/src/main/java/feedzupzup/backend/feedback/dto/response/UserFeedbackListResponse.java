package feedzupzup.backend.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "일반 사용자용 피드백 목록 응답")
public record UserFeedbackListResponse(
        @Schema(description = "피드백 목록")
        List<UserFeedbackItem> feedbacks,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "다음 커서 ID", example = "3")
        Long nextCursorId
) {

    public static UserFeedbackListResponse of(
            final List<FeedbackItem> feedbackItems,
            final boolean hasNext,
            final Long nextCursorId
    ) {
        return new UserFeedbackListResponse(
                convertUserFeedbackItemsFrom(feedbackItems),
                hasNext,
                nextCursorId
        );
    }

    private static List<UserFeedbackItem> convertUserFeedbackItemsFrom(List<FeedbackItem> feedbackItems) {
        return feedbackItems.stream()
                .map(UserFeedbackItem::from)
                .toList();
    }

}
