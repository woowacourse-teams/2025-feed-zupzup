package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedbacks;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "피드백 통계 응답")
public record StatisticResponse(
        @Schema(description = "반영률", example = "20")
        int reflectionRate,

        @Schema(description = "완료된 피드백 수", example = "1")
        int confirmedCount,

        @Schema(description = "대기중인 피드백 수", example = "4")
        int waitingCount,

        @Schema(description = "총 피드백 수", example = "5")
        int totalCount
) {
    public static StatisticResponse of(Feedbacks feedbacks) {
        return new StatisticResponse(
                feedbacks.calculateReflectionRate(),
                feedbacks.calculateConfirmedCount(),
                feedbacks.calculateWaitingCount(),
                feedbacks.getSize()
        );
    }
}
