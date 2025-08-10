package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "피드백 생성 응답")
public record CreateFeedbackResponse(
        @Schema(description = "피드백 ID", example = "1")
        Long feedbackId,

        @Schema(description = "피드백 내용", example = "급식실 음식 간이 너무 짜요")
        String content,

        @Schema(description = "처리 상태", example = "WAITING")
        ProcessStatus status,

        @Schema(description = "비밀 피드백 여부", example = "false")
        boolean isSecret,

        @Schema(description = "작성자 이름", example = "댕댕이")
        String userName,

        @Schema(description = "생성일시", example = "2025-07-12T09:30:00.000Z")
        LocalDateTime postedAt,

        @Schema(description = "카테고리", example = "시설")
        String category
) {

    public static CreateFeedbackResponse from(final Feedback feedback) {
        return new CreateFeedbackResponse(
                feedback.getId(),
                feedback.getContent().getValue(),
                feedback.getStatus(),
                feedback.isSecret(),
                feedback.getUserName(),
                feedback.getCreatedAt(),
                feedback.getOrganizationCategory().getCategory().getKoreanName()
        );
    }

}
