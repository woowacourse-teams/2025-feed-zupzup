package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse.AdminFeedbackItem;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ClusterFeedbacksResponse(
        @Schema(description = "특정 클러스터 전체 피드백 목록")
        List<AdminFeedbackItem> feedbacks,
        @Schema(description = "클러스터 대표 글", example = "클러스터 대표 글입니다")
        String label,
        @Schema(description = "클러스터에 해당하는 피드백 총 개수", example = "5")
        Long totalCount
) {

    public static ClusterFeedbacksResponse of(final List<Feedback> feedbacks, final String label, final Long totalCount) {
        final List<AdminFeedbackItem> feedbackItems = feedbacks.stream()
                .map(feedback -> AdminFeedbackItem.from(FeedbackItem.from(feedback)))
                .toList();
        return new ClusterFeedbacksResponse(feedbackItems, label, totalCount);
    }

    public static ClusterFeedbacksResponse of(final List<FeedbackEmbeddingCluster> embeddingClusters, final String label) {
        final List<AdminFeedbackItem> feedbackItems = embeddingClusters.stream()
                .map(embeddingCluster -> AdminFeedbackItem.from(FeedbackItem.from(embeddingCluster.getFeedback())))
                .toList();
        return new ClusterFeedbacksResponse(feedbackItems, label, (long) embeddingClusters.size());
    }
}
