package feedzupzup.backend.feedback.application.scheduler;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import feedzupzup.backend.feedback.application.FeedbackLikeCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FeedbackLikeScheduler {

    private final FeedbackLikeCounter feedbackLikeCounter;

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    @Scheduled(cron = "0 0 * * * *")
    public void syncFeedbackLikeCounter() {
        feedbackLikeCounter.syncLikesToDatabase();
    }
}
