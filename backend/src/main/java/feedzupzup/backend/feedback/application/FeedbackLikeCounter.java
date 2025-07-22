package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeInMemoryRepository;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FeedbackLikeCounter {

    private final FeedbackLikeInMemoryRepository feedbackLikeInMemoryRepository;
    private final FeedBackRepository feedBackRepository;

    public void increaseFeedbackLikeCount(final FeedbackPage feedbackPage) {
        feedbackPage.getFeedbacks().forEach(this::increaseLikeCount);
    }

    private void increaseLikeCount(final Feedback feedback) {
        feedback.updateLikeCount(feedback.getLikeCount() + feedbackLikeInMemoryRepository.get(feedback.getId()));
    }

    @Transactional
    public void syncLikesToDatabase() {
        final ConcurrentHashMap<Long, AtomicInteger> feedbackLikes = feedbackLikeInMemoryRepository.getFeedbackLikes();
        feedbackLikes.forEach((feedbackId, likeCount) -> feedBackRepository.findById(feedbackId)
                .ifPresent(feedback -> feedback.updateLikeCount(feedback.getLikeCount() + likeCount.get())));
        feedbackLikeInMemoryRepository.clear();
    }
}
