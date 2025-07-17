package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "피드백 비밀 상태 변경 응답")
public record UpdateFeedbackSecretResponse(
        @Schema(description = "피드백 ID", example = "1")
        Long feedbackId,

        @Schema(description = "비밀 피드백 여부", example = "true")
        boolean isSecret,

        @Schema(description = "수정일시", example = "2025-07-13T10:30:00.000Z")
        LocalDateTime modifiedAt
) {

        public static UpdateFeedbackSecretResponse from(Feedback feedback) {
                return new UpdateFeedbackSecretResponse(
                        feedback.getId(),
                        feedback.isSecret(),
                        feedback.getModifiedAt()
                );
        }
}
