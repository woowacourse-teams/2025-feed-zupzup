package feedzupzup.backend.feedback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드백 비밀 상태 변경 요청")
public record UpdateFeedbackSecretRequest(
        @Schema(description = "비밀 피드백 여부", example = "true")
        boolean isSecret
) {

}
