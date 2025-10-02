package feedzupzup.backend.feedback.domain.service;

import static feedzupzup.backend.global.config.CacheConfig.LATEST_FEEDBACK_CACHE;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LatestCacheHandler implements CacheHandler {

    private final CacheManager cacheManager;

    @Override
    public void handle(final FeedbackItem savedFeedbackItem, final UUID organizationUuid) {
        final Optional<List<FeedbackItem>> findCachedFeedbacks = getCachedFeedbacks(organizationUuid);
        if (findCachedFeedbacks.isEmpty()) {
            return;
        }
        final List<FeedbackItem> cachedFeedbackItems = findCachedFeedbacks.get();
        final List<FeedbackItem> mutableCaches = new ArrayList<>(cachedFeedbackItems);
        if (mutableCaches.size() >= MAX_CACHE_VALUE) {
            mutableCaches.removeLast();
        }
        mutableCaches.addFirst(savedFeedbackItem);
        cacheManager.getCache(LATEST_FEEDBACK_CACHE)
                .put(organizationUuid, mutableCaches);
    }

    private Optional<List<FeedbackItem>> getCachedFeedbacks(final UUID organizationUuid) {
        final Cache cache = cacheManager.getCache(LATEST_FEEDBACK_CACHE);
        if (cache == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(organizationUuid, List.class));
    }

}
