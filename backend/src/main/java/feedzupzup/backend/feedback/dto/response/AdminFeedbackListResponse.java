package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "관리자용 피드백 목록 응답")
public record AdminFeedbackListResponse(
        @Schema(description = "피드백 목록")
        List<AdminFeedbackItem> feedbacks,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "다음 커서 ID", example = "3")
        Long nextCursorId
) {

    public static AdminFeedbackListResponse from(final FeedbackPage feedbackPage) {
        final List<AdminFeedbackItem> adminFeedbackItems = feedbackPage.getFeedbacks().stream()
                .map(AdminFeedbackItem::from)
                .toList();
        return new AdminFeedbackListResponse(
                adminFeedbackItems,
                feedbackPage.isHasNext(),
                feedbackPage.calculateNextCursorId()
        );
    }

    @Schema(description = "관리자용 피드백 항목")
    public record AdminFeedbackItem(
            @Schema(description = "피드백 ID", example = "1")
            Long feedbackId,

            @Schema(description = "피드백 내용", example = "급식실 음식 간이 너무 짜요")
            String content,

            @Schema(description = "처리 상태", example = "WATING")
            ProcessStatus status,

            @Schema(description = "비밀 피드백 여부", example = "false")
            boolean isSecret,

            @Schema(description = "좋아요 수", example = "7")
            int likeCount,

            @Schema(description = "작성자 이름", example = "댕댕이")
            String userName,

            @Schema(description = "생성일시", example = "2025-07-12T09:30:00.000Z")
            LocalDateTime createdAt,

            @Schema(description = "카테고리", example = "시설")
            String category
    ) {

        private static AdminFeedbackItem from(final Feedback feedback) {
            return new AdminFeedbackItem(
                    feedback.getId(),
                    feedback.getContent(),
                    feedback.getStatus(),
                    feedback.isSecret(),
                    feedback.getLikeCount(),
                    feedback.getUserName(),
                    feedback.getCreatedAt(),
                    feedback.getCategory().getKoreaName()
            );
        }

    }
}
