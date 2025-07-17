package feedzupzup.backend.feedback.fixture;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.ProcessStatus;

public class FeedbackFixture {


    public static Feedback createFeedbackWithStatus(ProcessStatus status) {
        return Feedback.builder()
                .content("상태별 피드백")
                .isSecret(false)
                .status(status)
                .placeId(1L)
                .imageUrl("https://example.com/image.jpg")
                .build();
    }

    public static Feedback createFeedbackWithPlaceId(Long placeId) {
        return Feedback.builder()
                .content("장소별 피드백")
                .isSecret(false)
                .status(ProcessStatus.WAITING)
                .placeId(placeId)
                .imageUrl("https://example.com/image.jpg")
                .build();
    }

    public static Feedback createFeedbackWithSecret(final boolean isSecret) {
        return Feedback.builder()
                .content("장소별 피드백")
                .isSecret(isSecret)
                .status(ProcessStatus.WAITING)
                .placeId(1L)
                .imageUrl("https://example.com/image.jpg")
                .build();
    }
}
