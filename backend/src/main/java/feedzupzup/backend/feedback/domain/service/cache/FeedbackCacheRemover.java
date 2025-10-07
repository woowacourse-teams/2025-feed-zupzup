package feedzupzup.backend.feedback.domain.service.cache;

import static feedzupzup.backend.global.domain.CacheType.LATEST_FEEDBACK;
import static feedzupzup.backend.global.domain.CacheType.LIKES_FEEDBACK;
import static feedzupzup.backend.global.domain.CacheType.OLDEST_FEEDBACK;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackCacheRemover {

    private final CacheHelper cacheHelper;

    public void removeAllFeedbackCachesInfContains(
            final FeedbackItem feedbackItem,
            final UUID organizationUuid
    ) {
        removeCacheIfContains(feedbackItem, organizationUuid, LIKES_FEEDBACK.getCacheName());
        removeCacheIfContains(feedbackItem, organizationUuid, LATEST_FEEDBACK.getCacheName());
        removeCacheIfContains(feedbackItem, organizationUuid, OLDEST_FEEDBACK.getCacheName());
    }

    private void removeCacheIfContains(
            final FeedbackItem feedbackItem,
            final UUID organizationUuid,
            final String cacheName
    ) {
        cacheHelper.<FeedbackItem>getCacheValueList(organizationUuid, cacheName)
                .filter(items -> containsFeedback(items, feedbackItem))
                .ifPresent(items -> cacheHelper.removeCache(organizationUuid, cacheName));
    }

    private boolean containsFeedback(final List<FeedbackItem> items, final FeedbackItem feedbackItem) {
        return items.stream()
                .anyMatch(item -> item.feedbackId().equals(feedbackItem.feedbackId()));
    }

}
