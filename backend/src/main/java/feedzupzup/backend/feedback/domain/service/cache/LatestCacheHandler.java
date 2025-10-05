package feedzupzup.backend.feedback.domain.service.cache;

import static feedzupzup.backend.global.domain.CacheType.LATEST_FEEDBACK;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LatestCacheHandler implements FeedbackCacheHandler {

    private final CacheHelper cacheHelper;

    @Override
    public void handle(final FeedbackItem savedFeedbackItem, final UUID organizationUuid) {
        final Optional<List<FeedbackItem>> findCachedFeedbacks = cacheHelper.getCacheValueList(organizationUuid,
                LATEST_FEEDBACK.getCacheName());
        if (findCachedFeedbacks.isEmpty()) {
            return;
        }
        final List<FeedbackItem> cachedFeedbackItems = findCachedFeedbacks.get();
        final List<FeedbackItem> mutableCaches = new ArrayList<>(cachedFeedbackItems);
        if (mutableCaches.size() >= MAX_CACHE_VALUE) {
            mutableCaches.removeLast();
        }
        mutableCaches.addFirst(savedFeedbackItem);
        cacheHelper.putInCache(LATEST_FEEDBACK.getCacheName(), organizationUuid, mutableCaches);
    }
}
