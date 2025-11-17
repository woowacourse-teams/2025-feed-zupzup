package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.organization.domain.FeedbackAmount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "조직 통계 응답")
public record OrganizationStatisticResponse(

        @Schema(description = "반영률", example = "20")
        int reflectionRate,

        @Schema(description = "완료된 피드백 수", example = "1")
        long confirmedCount,

        @Schema(description = "대기중인 피드백 수", example = "4")
        long waitingCount,

        @Schema(description = "총 피드백 수", example = "5")
        long totalCount
) {
    public static OrganizationStatisticResponse of(final FeedbackAmount feedbackAmount) {
        return new OrganizationStatisticResponse(
                feedbackAmount.calculateReflectionRate(),
                feedbackAmount.getFeedbackConfirmedCount(),
                feedbackAmount.getFeedbackWaitingCount(),
                feedbackAmount.getFeedbackTotalCount()
        );
    }
}
