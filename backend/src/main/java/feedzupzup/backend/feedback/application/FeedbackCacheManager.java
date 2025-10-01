package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.feedback.application.FeedbackCacheManager.LikeAction.*;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackCacheManager {

    private static final int MAX_CACHE_VALUE = 10;

    private final CacheManager cacheManager;

    public enum LikeAction {
        INCREASE, DECREASE
    }

    public void handleLatestCache(
            final FeedbackItem savedFeedbackItem,
            final UUID organizationUuid
    ) {
        final String latestCacheName = "latestFeedbacks";
        final Optional<List<FeedbackItem>> findCachedFeedbacks = getCachedFeedbacks(organizationUuid,
                latestCacheName);

        if (findCachedFeedbacks.isEmpty()) {
            return;
        }
        final List<FeedbackItem> cachedFeedbackItems = findCachedFeedbacks.get();

        List<FeedbackItem> mutableCaches = new ArrayList<>(cachedFeedbackItems);

        if (mutableCaches.size() >= 10) {
            mutableCaches.removeLast();
        }
        mutableCaches.addFirst(savedFeedbackItem);

        cacheManager.getCache("latestFeedbacks")
                .put(organizationUuid, mutableCaches);
    }

    public void handleLikesCache(
            final FeedbackItem savedFeedbackItem,
            final UUID organizationUuid,
            final LikeAction likeAction
    ) {
        final String likesCacheName = "likesFeedbacks";
        final Optional<List<FeedbackItem>> findCachedFeedbacks = getCachedFeedbacks(organizationUuid, likesCacheName);
        if (findCachedFeedbacks.isEmpty()) {
            return;
        }
        final List<FeedbackItem> cachedFeedbacks = findCachedFeedbacks.get();

        if (likeAction == INCREASE) {
            handleLikesCacheOnIncrease(savedFeedbackItem, cachedFeedbacks, organizationUuid);
            return;
        }
        if (likeAction == DECREASE) {
            handleLikesCacheOnDecrease(savedFeedbackItem, cachedFeedbacks, organizationUuid);
        }
    }

    private Optional<List<FeedbackItem>> getCachedFeedbacks(final UUID organizationUuid, String cacheName) {
        final Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(organizationUuid, List.class));
    }

    private void handleLikesCacheOnIncrease(
            final FeedbackItem savedFeedbackItem,
            final List<FeedbackItem> cachedFeedbackItems,
            final UUID organizationUuid
    ) {
        List<FeedbackItem> mutableCaches = new ArrayList<>(cachedFeedbackItems);

        mutableCaches.removeIf(
                cachedFeedback -> cachedFeedback.feedbackId().equals(savedFeedbackItem.feedbackId())
        );

        mutableCaches.add(savedFeedbackItem);
        sortCachedFeedback(mutableCaches);
        if (cachedFeedbackItems.size() > MAX_CACHE_VALUE) {
            mutableCaches.removeLast();
        }
        cacheManager.getCache("likesFeedbacks")
                .put(organizationUuid, mutableCaches);
    }

    private void handleLikesCacheOnDecrease(
            final FeedbackItem savedFeedbackItem,
            final List<FeedbackItem> cachedFeedbacks,
            final UUID organizationUuid
    ) {
        final boolean isAlreadyCached = isCachedFeedback(savedFeedbackItem, cachedFeedbacks);
        if (!isAlreadyCached) {
            return;
        }
        if (isBelowCacheThreshold(savedFeedbackItem, cachedFeedbacks)) {
            final Cache likesCache = cacheManager.getCache("likesFeedbacks");
            likesCache.evict(organizationUuid);
            return;
        }
        reorganizeCachedFeedbacks(savedFeedbackItem, cachedFeedbacks, organizationUuid);
    }

    private boolean isBelowCacheThreshold(
            final FeedbackItem savedFeedbackItem,
            final List<FeedbackItem> cachedFeedbacks
    ) {
        final FeedbackItem lastCachedFeedback = cachedFeedbacks.getLast();
        return savedFeedbackItem.likeCount() < lastCachedFeedback.likeCount();
    }

    private void reorganizeCachedFeedbacks(
            final FeedbackItem savedFeedbackItem,
            final List<FeedbackItem> cachedFeedbackItems,
            final UUID organizationUuid
    ) {
        List<FeedbackItem> mutableCaches = new ArrayList<>(cachedFeedbackItems);
        mutableCaches.removeIf(
                cachedFeedback -> cachedFeedback.feedbackId().equals(savedFeedbackItem.feedbackId())
        );

        mutableCaches.add(savedFeedbackItem);
        sortCachedFeedback(mutableCaches);
        cacheManager.getCache("likesFeedbacks")
                .put(organizationUuid, mutableCaches);
    }

    private void sortCachedFeedback(final List<FeedbackItem> cachedFeedbacks) {
        cachedFeedbacks.sort(Comparator.comparingInt(FeedbackItem::likeCount).reversed()
                .thenComparing(FeedbackItem::feedbackId));
    }

    private boolean isCachedFeedback(
            final FeedbackItem savedFeedbackItem,
            final List<FeedbackItem> cachedFeedbacks
    ) {
        return cachedFeedbacks.stream()
                .anyMatch(cachedFeedback -> cachedFeedback.feedbackId().equals(savedFeedbackItem.feedbackId()));
    }
}
