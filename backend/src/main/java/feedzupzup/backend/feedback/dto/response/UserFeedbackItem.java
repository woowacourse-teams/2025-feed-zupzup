package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "피드백 항목")
public record UserFeedbackItem(
        @Schema(description = "피드백 ID", example = "1")
        Long feedbackId,

        @Schema(description = "피드백 내용", example = "급식실 음식 간이 너무 짜요")
        String content,

        @Schema(description = "처리 상태", example = "WAITING")
        ProcessStatus status,

        @Schema(description = "비밀 피드백 여부", example = "false")
        boolean isSecret,

        @Schema(description = "좋아요 수", example = "5")
        int likeCount,

        @Schema(description = "작성자 이름", example = "댕댕이")
        String userName,

        @Schema(description = "생성일시", example = "2025-07-12T09:30:00.000Z")
        LocalDateTime postedAt,

        @Schema(description = "카테고리", example = "시설")
        String category,

        @Schema(description = "답변 내용", example = "빠른 시일 내로 개선하겠습니다.")
        String comment,

        @Schema(description = "이미지 URL", example = "https://example.com/image.png")
        String imageUrl
) {

    public static UserFeedbackItem from(final FeedbackItem feedbackItem) {
        return new UserFeedbackItem(
                feedbackItem.feedbackId(),
                feedbackItem.content(),
                feedbackItem.status(),
                feedbackItem.isSecret(),
                feedbackItem.likeCount(),
                feedbackItem.userName(),
                feedbackItem.postedAt(),
                feedbackItem.category(),
                feedbackItem.comment(),
                feedbackItem.imageUrl()
        );
    }
}
