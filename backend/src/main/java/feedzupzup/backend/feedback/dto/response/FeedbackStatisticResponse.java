package feedzupzup.backend.feedback.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FeedbackStatisticResponse(
        @Schema(description = "완료된 피드백 수", example = "16")
        long confirmedCount,
        @Schema(description = "총 피드백 수", example = "5")
        long totalCount,
        @Schema(description = "반영률", example = "80")
        int reflectionRate
) {

}
