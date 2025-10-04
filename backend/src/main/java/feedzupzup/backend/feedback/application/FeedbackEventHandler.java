package feedzupzup.backend.feedback.application;

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
        return cacheHandlers.get(type.getCacheType().getHandlerName());
    }
}
