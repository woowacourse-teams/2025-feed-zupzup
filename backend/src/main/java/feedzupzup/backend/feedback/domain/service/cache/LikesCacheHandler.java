package feedzupzup.backend.feedback.domain.service.cache;

import static feedzupzup.backend.global.domain.CacheType.*;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikesCacheHandler implements FeedbackCacheHandler {

    private final CacheHelper cacheHelper;
    private final FeedbackCacheRemover feedbackCacheRemover;

    @Override
    public void handle(final FeedbackItem savedFeedbackItem, final UUID organizationUuid) {
        feedbackCacheRemover.removeAllFeedbackCachesInfContains(savedFeedbackItem, organizationUuid);
        handleLikesCache(savedFeedbackItem, organizationUuid);
    }

    private void handleLikesCache(final FeedbackItem savedFeedbackItem, final UUID organizationUuid) {
        final Optional<List<FeedbackItem>> findCachedFeedbacks = cacheHelper.getCacheValueList(
                organizationUuid, LIKES_FEEDBACK.getCacheName());
        if (findCachedFeedbacks.isEmpty()) {
            return;
        }
        final List<FeedbackItem> cachedFeedbacks = findCachedFeedbacks.get();
        if (isCacheClearCondition(savedFeedbackItem, cachedFeedbacks)) {
            cacheHelper.removeCache(organizationUuid, LIKES_FEEDBACK.getCacheName());
        }
    }

    private boolean isCacheClearCondition(
            final FeedbackItem savedFeedbackItem,
            final List<FeedbackItem> cachedFeedbacks
    ) {
        final FeedbackItem lastCachedFeedback = cachedFeedbacks.getLast();
        return savedFeedbackItem.likeCount() >= lastCachedFeedback.likeCount();
    }
}