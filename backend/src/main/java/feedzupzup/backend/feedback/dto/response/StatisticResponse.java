package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.FeedbackAmount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드백 통계 응답")
public record StatisticResponse(
        @Schema(description = "반영률", example = "20")
        int reflectionRate,

        @Schema(description = "완료된 피드백 수", example = "1")
        long confirmedCount,

        @Schema(description = "대기중인 피드백 수", example = "4")
        long waitingCount,

        @Schema(description = "총 피드백 수", example = "5")
        long totalCount
) {
    public static StatisticResponse of(final FeedbackAmount feedbackAmount, int reflectionRate) {
        return new StatisticResponse(
                reflectionRate,
                feedbackAmount.confirmedCount(),
                feedbackAmount.waitingCount(),
                feedbackAmount.totalCount()
        );
    }
}
