package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.ClusterRepresentativeFeedback;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

public record ClusterRepresentativeFeedbacksResponse(
        @Schema(description = " 클러스터 대표 전체 목록")
        List<ClusterRepresentativeFeedbackResponse> clusterRepresentativeFeedbacks
) {

    public record ClusterRepresentativeFeedbackResponse(
            @Schema(description = "클러스터 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            UUID clusterId,
            @Schema(description = "대표 내용", example = "클러스터의 대표 내용")
            String content,
            @Schema(description = "클러스터에 속한 피드백 총 개수", example = "5")
            Long totalCount
    ) {

        public static ClusterRepresentativeFeedbackResponse from(ClusterRepresentativeFeedback feedback) {
            return new ClusterRepresentativeFeedbackResponse(feedback.clusterUuid(), feedback.content(), feedback.totalCount());
        }
    }

    public static ClusterRepresentativeFeedbacksResponse from(List<ClusterRepresentativeFeedback> feedbacks) {
        List<ClusterRepresentativeFeedbackResponse> responses = feedbacks.stream()
                .map(ClusterRepresentativeFeedbackResponse::from)
                .toList();
        return new ClusterRepresentativeFeedbacksResponse(responses);
    }
}
