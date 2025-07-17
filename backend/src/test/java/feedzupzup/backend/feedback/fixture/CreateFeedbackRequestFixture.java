package feedzupzup.backend.feedback.fixture;

import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;

public class CreateFeedbackRequestFixture {

    public static CreateFeedbackRequest createRequestWithContent(String content) {
        return new CreateFeedbackRequest(
                content,
                "https://example.com/image.jpg",
                false
        );
    }
}
