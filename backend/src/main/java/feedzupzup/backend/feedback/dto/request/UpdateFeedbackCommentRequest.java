package feedzupzup.backend.feedback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "답글 추가 요청")
public record UpdateFeedbackCommentRequest(
        @Schema(description = "답글 내용", example = "완료했습니다.")
        String comment
) {

}
