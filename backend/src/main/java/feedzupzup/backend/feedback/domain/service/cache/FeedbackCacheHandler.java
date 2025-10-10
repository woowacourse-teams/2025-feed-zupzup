package feedzupzup.backend.feedback.domain.service.cache;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.UUID;

public interface FeedbackCacheHandler extends CacheHandler<FeedbackItem, UUID> {

    int MAX_CACHE_VALUE = 10;
}
