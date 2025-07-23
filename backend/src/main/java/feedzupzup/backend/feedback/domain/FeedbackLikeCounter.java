package feedzupzup.backend.feedback.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackLikeCounter {

    private final FeedbackLikeInMemoryRepository feedbackLikeInMemoryRepository;

    public void applyBufferedLikeCount(final FeedbackPage feedbackPage) {
        final List<Feedback> feedbacks = feedbackPage.getFeedbacks();
        for (Feedback feedback : feedbacks) {
            applyBufferedLike(feedback);
        }
    }

    private void applyBufferedLike(final Feedback feedback) {
        feedback.updateLikeCount(feedback.getLikeCount() + feedbackLikeInMemoryRepository.getLikeCount(feedback.getId()));
    }
}
