package feedzupzup.backend.feedback.fixture;

import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;

public class FeedbackRequestFixture {

    public static CreateFeedbackRequest createRequestWithContent(final String content) {
        return new CreateFeedbackRequest(
                content,
                false,
                "테스트유저"
        );
    }

    public static CreateFeedbackRequest createRequestWithSecret(final boolean isSecret) {
        return new CreateFeedbackRequest(
                "내용",
                isSecret,
                "테스트유저"
        );
    }
}
