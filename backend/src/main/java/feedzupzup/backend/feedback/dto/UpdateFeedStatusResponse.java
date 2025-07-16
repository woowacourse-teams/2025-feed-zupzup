package feedzupzup.backend.feedback.dto;

import feedzupzup.backend.feedback.domain.ProcessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "피드백 처리 상태 변경 응답")
public record UpdateFeedStatusResponse(
    @Schema(description = "피드백 ID", example = "1")
    Long feedbackId,

    @Schema(description = "피드백 처리 상태", example = "WAITING")
    ProcessStatus status,

    @Schema(description = "수정일시", example = "2025-07-13T10:30:00.000Z")
    LocalDateTime modifiedAt
) {

}
