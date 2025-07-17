package feedzupzup.backend.feedback.fixture;

import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;

public class FeedbackRequestFixture {

    public static CreateFeedbackRequest createRequestWithContent(String content) {
        return new CreateFeedbackRequest(
                content,
                "https://example.com/image.jpg",
                false
        );
    }

    public static CreateFeedbackRequest createRequestWithSecret(final boolean isSecret) {
        return new CreateFeedbackRequest(
                "내용",
                "https://example.com/image.jpg",
                isSecret
        );
    }
}
