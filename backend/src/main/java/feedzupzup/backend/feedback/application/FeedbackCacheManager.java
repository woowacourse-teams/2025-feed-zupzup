package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.feedback.application.FeedbackCacheManager.LikeAction.*;

import feedzupzup.backend.feedback.domain.Feedback;
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

    public void handleLikesCache(final Feedback savedFeedback, final LikeAction likeAction) {
        final Optional<List<Feedback>> findCachedFeedbacks = getCachedFeedbacks(savedFeedback);
        if (findCachedFeedbacks.isEmpty()) {
            return;
        }
        final List<Feedback> cachedFeedbacks = findCachedFeedbacks.get();

        if (likeAction == INCREASE) {
            handleLikesCacheOnIncrease(savedFeedback, cachedFeedbacks);
            return;
        }
        if (likeAction == DECREASE) {
            handleLikesCacheOnDecrease(savedFeedback, cachedFeedbacks);
        }
    }

    private Optional<List<Feedback>> getCachedFeedbacks(final Feedback savedFeedback) {
        final UUID organizationUuid = savedFeedback.getOrganization().getUuid();
        final Cache likesFeedbacks = cacheManager.getCache("likesFeedbacks");
        if (likesFeedbacks == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(likesFeedbacks.get(organizationUuid, List.class));
    }

    private void handleLikesCacheOnIncrease(
            final Feedback savedFeedback,
            final List<Feedback> cachedFeedbacks
    ) {
        cachedFeedbacks.removeIf(
                cachedFeedback -> cachedFeedback.getId().equals(savedFeedback.getId())
        );
        cachedFeedbacks.add(savedFeedback);
        sortCachedFeedback(cachedFeedbacks);
        if (cachedFeedbacks.size() > MAX_CACHE_VALUE) {
            cachedFeedbacks.removeLast();
        }
    }

    private void handleLikesCacheOnDecrease(
            final Feedback savedFeedback,
            final List<Feedback> cachedFeedbacks
    ) {
        final boolean isAlreadyCached = isCachedFeedback(savedFeedback, cachedFeedbacks);
        if (!isAlreadyCached) {
            return;
        }
        if (isBelowCacheThreshold(savedFeedback, cachedFeedbacks)) {
            cachedFeedbacks.clear();
            return;
        }
        reorganizeCachedFeedbacks(savedFeedback, cachedFeedbacks);
    }

    private boolean isBelowCacheThreshold(final Feedback savedFeedback, final List<Feedback> cachedFeedbacks) {
        final Feedback lastCachedFeedback = cachedFeedbacks.getLast();
        return savedFeedback.getLikeCountValue() < lastCachedFeedback.getLikeCountValue();
    }

    private void reorganizeCachedFeedbacks(
            final Feedback savedFeedback,
            final List<Feedback> cachedFeedbacks
    ) {
        cachedFeedbacks.removeIf(
                cachedFeedback -> cachedFeedback.getId().equals(savedFeedback.getId())
        );
        cachedFeedbacks.add(savedFeedback);
        sortCachedFeedback(cachedFeedbacks);
    }

    private void sortCachedFeedback(final List<Feedback> cachedFeedbacks) {
        cachedFeedbacks.sort(Comparator.comparingInt(Feedback::getLikeCountValue).reversed()
                .thenComparing(Feedback::getId));
    }

    private boolean isCachedFeedback(
            final Feedback savedFeedback,
            final List<Feedback> cachedFeedbacks
    ) {
        return cachedFeedbacks.stream()
                .anyMatch(cachedFeedback -> cachedFeedback.getId().equals(savedFeedback.getId()));
    }
}
