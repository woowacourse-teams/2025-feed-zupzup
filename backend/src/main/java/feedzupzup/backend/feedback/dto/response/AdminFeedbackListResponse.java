package feedzupzup.backend.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "관리자용 피드백 목록 응답")
public record AdminFeedbackListResponse(
        @Schema(description = "피드백 목록")
        List<AdminFeedbackItem> feedbacks,
        
        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,
        
        @Schema(description = "다음 커서 ID", example = "3")
        Long nextCursorId
) {}