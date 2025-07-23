package feedzupzup.backend.feedback.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FeedbackLikeInMemoryRepository {

    private static final ConcurrentHashMap<Long, AtomicInteger> feedbackLikes = new ConcurrentHashMap<>();

    public int increaseAndGet(final Long feedbackId) {
        return feedbackLikes.computeIfAbsent(feedbackId, id -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public int decreaseAndGet(final Long feedbackId) {
        return feedbackLikes.computeIfAbsent(feedbackId, id -> new AtomicInteger(0))
                .decrementAndGet();
    }

    public int getLikeCount(final Long feedbackId) {
        final AtomicInteger likeCount = feedbackLikes.get(feedbackId);
        if (likeCount == null) {
            return 0;
        }
        return likeCount.get();
    }

    public Map<Long, Integer> getLikeCounts() {
        return feedbackLikes.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));
    }

    public void clear() {
        feedbackLikes.clear();
    }

}
