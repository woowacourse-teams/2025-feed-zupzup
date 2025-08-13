package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "일반 사용자용 피드백 목록 응답")
public record UserFeedbackListResponse(
        @Schema(description = "피드백 목록")
        List<FeedbackItem> feedbacks,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "다음 커서 ID", example = "3")
        Long nextCursorId
) {

    public static UserFeedbackListResponse of(
            final List<Feedback> feedbacks,
            final boolean hasNext,
            final Long nextCursorId
    ) {
        final List<FeedbackItem> userFeedbackItems = feedbacks.stream()
                .map(FeedbackItem::from)
                .toList();
        return new UserFeedbackListResponse(
                userFeedbackItems,
                hasNext,
                nextCursorId
        );
    }

}
