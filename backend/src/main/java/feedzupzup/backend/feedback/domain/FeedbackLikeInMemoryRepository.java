package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FeedbackLikeInMemoryRepository {

    private final ConcurrentHashMap<Long, AtomicInteger> feedbackLikes = new ConcurrentHashMap<>();

    public int increase(final Long feedbackId) {
        return feedbackLikes.computeIfAbsent(feedbackId, id -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public int decrease(final Long feedbackId) {
        final AtomicInteger likeCount = feedbackLikes.get(feedbackId);
        if (likeCount == null) {
            throw new ResourceNotFoundException("좋아요 횟수는 음수가 될 수 없습니다.");
        }
        return likeCount.decrementAndGet();
    }

    public int get(final Long feedbackId) {
        final AtomicInteger likeCount = feedbackLikes.get(feedbackId);
        return likeCount != null ? likeCount.get() : 0;
    }

    public void clear() {
        feedbackLikes.clear();
    }

}
