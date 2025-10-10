package feedzupzup.backend.feedback.domain.service.cache;

import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OldestCacheHandler implements FeedbackCacheHandler {

    private final FeedbackCacheRemover feedbackCacheRemover;

    @Override
    public void handle(final FeedbackItem savedFeedbackItem, final UUID organizationUuid) {
        feedbackCacheRemover.removeAllFeedbackCachesInfContains(savedFeedbackItem, organizationUuid);
    }
}
