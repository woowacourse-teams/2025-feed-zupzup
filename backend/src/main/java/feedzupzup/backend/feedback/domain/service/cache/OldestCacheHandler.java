package feedzupzup.backend.feedback.domain.service.cache;

import static feedzupzup.backend.global.config.CacheConfig.OLDEST_FEEDBACK_CACHE;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OldestCacheHandler implements FeedbackCacheHandler {

    private final CacheHelper cacheHelper;

    @Override
    public void handle(final FeedbackItem savedFeedbackItem, final UUID organizationUuid) {
        final Optional<List<FeedbackItem>> findCachedFeedbacks = cacheHelper.getCacheValueList(
                organizationUuid, OLDEST_FEEDBACK_CACHE);
        if (findCachedFeedbacks.isEmpty()) {
            return;
        }

        final List<FeedbackItem> feedbackItems = findCachedFeedbacks.get();

        final boolean alreadyCached = feedbackItems.stream()
                .anyMatch(feedbackItem -> feedbackItem.feedbackId().equals(savedFeedbackItem.feedbackId()));

        if (alreadyCached) {
            cacheHelper.removeCache(organizationUuid, OLDEST_FEEDBACK_CACHE);
        }
    }
}
