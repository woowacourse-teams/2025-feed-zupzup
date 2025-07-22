package feedzupzup.backend.feedback.domain;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FeedbackLikeCounter {

    private final FeedbackLikeInMemoryRepository feedbackLikeInMemoryRepository;
    private final FeedBackRepository feedBackRepository;

    public List<Feedback> getIncreasedLikeFeedbacks(@NonNull final FeedbackPage feedbackPage) {
        return feedbackPage.getFeedbacks().stream()
                .map(this::copyOfLikeCount)
                .toList();
    }

    private Feedback copyOfLikeCount(final Feedback feedback) {
        final int likeCount = feedback.getLikeCount() + feedbackLikeInMemoryRepository.get(feedback.getId());
        return feedback.copyOfLikeCount(likeCount);
    }

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    @Scheduled(cron = "0 0 * * * *")
    public void syncLikesToDatabase() {
        final ConcurrentHashMap<Long, AtomicInteger> feedbackLikes = feedbackLikeInMemoryRepository.getFeedbackLikes();
        feedbackLikes.forEach((feedbackId, likeCount) -> feedBackRepository.findById(feedbackId)
                .ifPresent(feedback -> feedback.updateLikeCount(feedback.getLikeCount() + likeCount.get())));
        feedbackLikeInMemoryRepository.clear();
    }
}
