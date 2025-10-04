package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.LATEST;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.LIKES;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.OLDEST;

import feedzupzup.backend.feedback.domain.service.cache.FeedbackCacheHandler;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.event.FeedbackCacheEvent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FeedbackEventHandler {

    private final Map<String, FeedbackCacheHandler> cacheHandlers;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleFeedbackCache(final FeedbackCacheEvent event) {
        final FeedbackCacheHandler handler = findHandler(event.type());
        handler.handle(event.feedbackItem(), event.key());
    }

    private FeedbackCacheHandler findHandler(final FeedbackSortType type) {
        if (type == LATEST) {
            return cacheHandlers.get("latestCacheHandler");
        }
        if (type == LIKES) {
            return cacheHandlers.get("likesCacheHandler");
        }
        if (type == OLDEST) {
            return cacheHandlers.get("oldestCacheHandler");
        }
        throw new IllegalStateException("존재하지 않는 핸들러입니다.");
    }
}
