package feedzupzup.backend.feedback.domain.service.cache;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.UUID;

public class OldestCacheHandler implements CacheHandler {

    @Override
    public void handle(final FeedbackItem savedFeedbackItem, final UUID organizationUuid) {
    }
}
