package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "답글 추가 응답")
public record UpdateFeedbackCommentResponse(

        @Schema(description = "피드백 ID ", example = "1")
        Long feedbackId,

        @Schema(description = "답글", example = "처리했습니다.")
        String comment
) {

    public static UpdateFeedbackCommentResponse from(final Feedback feedback) {
        return new UpdateFeedbackCommentResponse(
                feedback.getId(),
                feedback.getComment().getValue()
        );
    }
}
