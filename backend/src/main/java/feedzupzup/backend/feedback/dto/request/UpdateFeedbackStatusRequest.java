package feedzupzup.backend.feedback.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드백 상태 변경 요청")
public record UpdateFeedbackStatusRequest(
        @Schema(description = "처리 상태", example = "WAITING", allowableValues = {"WAITING", "CONFIRMED"})
        String status
) {}
