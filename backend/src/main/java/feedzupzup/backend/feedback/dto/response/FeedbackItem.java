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
        String comment,
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
                feedback.getComment() == null ? null : feedback.getComment().getValue(),
                feedback.getImageUrl() == null ? null : feedback.getImageUrl().getValue()
        );
    }

    public FeedbackItem withMaskedContent() {
        return new FeedbackItem(
                this.feedbackId,
                "비밀글 입니다.",
                this.status,
                this.isSecret,
                this.likeCount,
                this.userName,
                this.postedAt,
                this.category,
                this.comment,
                this.imageUrl
        );
    }
}
