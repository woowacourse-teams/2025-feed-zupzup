package feedzupzup.backend.feedback.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackLikeCounter {

    private final FeedbackLikeRepository feedbackLikeRepository;


    public void applyBufferedLikeCount(final List<Feedback> feedbacks) {
        for (Feedback feedback : feedbacks) {
            applyBufferedLike(feedback);
        }
    }

    private void applyBufferedLike(final Feedback feedback) {
        feedback.updateLikeCount(feedback.getLikeCount() + feedbackLikeRepository.getLikeCount(feedback.getId()));
    }
}
