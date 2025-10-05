package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import java.time.LocalDateTime;

public record FeedbackItem(
        Long feedbackId,
        String content,
        ProcessStatus status,
        boolean isSecret,
        int likeCount,
        String userName,
        LocalDateTime postedAt,
        String category,

        @Schema(description = "답변 내용", example = "빠른 시일 내로 개선하겠습니다.")
        String comment,

        @Schema(description = "이미지 URL", example = "https://example.com/image.png")
        String imageUrl
) {
    public static FeedbackItem from(final Feedback feedback) {
        return new FeedbackItem(
                feedback.getId(),
                feedback.getContent().getValue(),
                feedback.getStatus(),
                feedback.isSecret(),
                feedback.getLikeCount().getValue(),
                feedback.getUserName().getValue(),
                feedback.getPostedAt().getValue(),
                feedback.getOrganizationCategory().getCategory().getKoreanName(),
                feedback.getComment() != null ? feedback.getComment().getValue() : null,
                feedback.getImageUrl() != null ? feedback.getImageUrl().getValue() : null
        );
    }

}
