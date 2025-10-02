package feedzupzup.backend.feedback.domain.service.cache;

import static feedzupzup.backend.global.config.CacheConfig.LIKES_FEEDBACK_CACHE;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikesCacheHandler implements CacheHandler {

    private final CacheHelper cacheHelper;

    @Override
    public void handle(final FeedbackItem savedFeedbackItem, final UUID organizationUuid) {
        final Optional<List<FeedbackItem>> findCachedFeedbacks = cacheHelper.getCacheValueList(
                organizationUuid, LIKES_FEEDBACK_CACHE);
        if (findCachedFeedbacks.isEmpty()) {
            return;
        }
        final List<FeedbackItem> cachedFeedbacks = findCachedFeedbacks.get();
        if (isCacheClearCondition(savedFeedbackItem, cachedFeedbacks)) {
            cacheHelper.removeCache(organizationUuid, LIKES_FEEDBACK_CACHE);
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