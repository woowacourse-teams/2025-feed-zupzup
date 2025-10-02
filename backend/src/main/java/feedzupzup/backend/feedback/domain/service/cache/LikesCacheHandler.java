package feedzupzup.backend.feedback.domain.service.cache;

import static feedzupzup.backend.global.config.CacheConfig.LIKES_FEEDBACK_CACHE;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikesCacheHandler implements CacheHandler {

    private final CacheManager cacheManager;

    @Override
    public void handle(final FeedbackItem savedFeedbackItem, final UUID organizationUuid) {
        final Optional<List<FeedbackItem>> findCachedFeedbacks = getCachedFeedbacks(organizationUuid);
        if (findCachedFeedbacks.isEmpty()) {
            return;
        }
        final List<FeedbackItem> cachedFeedbacks = findCachedFeedbacks.get();
        if (isCacheClearCondition(savedFeedbackItem, cachedFeedbacks)) {
            final Cache likesCache = cacheManager.getCache(LIKES_FEEDBACK_CACHE);
            likesCache.evict(organizationUuid);
        }
    }

    private boolean isCacheClearCondition(
            final FeedbackItem savedFeedbackItem,
            final List<FeedbackItem> cachedFeedbacks
    ) {
        final FeedbackItem lastCachedFeedback = cachedFeedbacks.getLast();
        return savedFeedbackItem.likeCount() >= lastCachedFeedback.likeCount();
    }

    private Optional<List<FeedbackItem>> getCachedFeedbacks(final UUID organizationUuid) {
        final Cache cache = cacheManager.getCache(LIKES_FEEDBACK_CACHE);
        if (cache == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(organizationUuid, List.class));
    }
}