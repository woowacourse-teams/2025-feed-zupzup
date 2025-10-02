package feedzupzup.backend.feedback.domain.service.cache;


import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.UUID;

public interface CacheHandler {

    int MAX_CACHE_VALUE = 10;

    void handle(FeedbackItem savedFeedbackItem, UUID organizationUuid);
}
