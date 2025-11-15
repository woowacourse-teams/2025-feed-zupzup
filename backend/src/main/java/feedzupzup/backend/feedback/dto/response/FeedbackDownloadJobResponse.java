package feedzupzup.backend.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드백 다운로드 작업 생성 응답")
public record FeedbackDownloadJobResponse(
        @Schema(description = "작업 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String jobId
) {

}
