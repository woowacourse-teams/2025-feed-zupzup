package feedzupzup.backend.feedback.dto.request;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드백 생성 요청")
public record CreateFeedbackRequest(
        @Schema(description = "피드백 내용", example = "급식실 음식 간이 너무 짜요")
        String content,

        @Schema(description = "이미지 URL", example = "https://bucket.s3.amazonaws.com/posts/uuid-image.jpg")
        String imageUrl,

        @Schema(description = "비밀 피드백 여부", example = "false")
        boolean isSecret
) {

    public Feedback toFeedback(final Long placeId) {
        return Feedback.builder()
                .content(content)
                .imageUrl(imageUrl)
                .placeId(placeId)
                .status(ProcessStatus.WATING)
                .isSecret(isSecret)
                .build();
    }
}
