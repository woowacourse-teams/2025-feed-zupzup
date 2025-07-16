package feedzupzup.backend.feedback.dto.response;

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

        @Schema(description = "관리자용 피드백 항목")
        record AdminFeedbackItem(
            @Schema(description = "피드백 ID", example = "1")
            Long feedbackId,

            @Schema(description = "피드백 내용", example = "급식실 음식 간이 너무 짜요")
            String content,

            @Schema(description = "처리 상태", example = "WATING")
            ProcessStatus status,

            @Schema(description = "이미지 URL", example = "https://bucket.s3.amazonaws.com/posts/uuid-image.jpg")
            String imageUrl,

            @Schema(description = "비밀 피드백 여부", example = "false")
            boolean isSecret,

            @Schema(description = "좋아요 수", example = "7")
            int likeCount,

            @Schema(description = "생성일시", example = "2025-07-12T09:30:00.000Z")
            LocalDateTime createdAt
        ) {}
}
