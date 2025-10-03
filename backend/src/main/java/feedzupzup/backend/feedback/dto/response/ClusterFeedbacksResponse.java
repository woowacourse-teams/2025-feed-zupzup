package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ClusterFeedbacksResponse(
        @Schema(description = "특정 클러스터 전체 피드백 목록")
        List<FeedbackItem> feedbacks
) {

    public static ClusterFeedbacksResponse of(final List<Feedback> feedbacks) {
        final List<FeedbackItem> myFeedbackItems = feedbacks.stream()
                .map(FeedbackItem::from)
                .toList();
        return new ClusterFeedbacksResponse(myFeedbackItems);
    }
}
