package feedzupzup.backend.feedback.application.scheduler;

import feedzupzup.backend.feedback.application.FeedbackLikeCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackLikeScheduler {

    private final FeedbackLikeCounter feedbackLikeCounter;

    @Async
    @Scheduled(cron = "0 0 * * * *")
    public void syncFeedbackLikeCounter() {
        feedbackLikeCounter.syncLikesToDatabase();
    }
}
