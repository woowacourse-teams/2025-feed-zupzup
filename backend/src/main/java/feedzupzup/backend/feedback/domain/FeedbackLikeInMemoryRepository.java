package feedzupzup.backend.feedback.domain;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class FeedbackLikeInMemoryRepository {

    private final ConcurrentHashMap<Long, AtomicInteger> feedbackLikes = new ConcurrentHashMap<>();

    public int increase(final long feedbackId) {
        return feedbackLikes.computeIfAbsent(feedbackId, id -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public int decrease(final long feedbackId) {
        return Objects.requireNonNull(feedbackLikes.computeIfPresent(feedbackId, (id, count) -> {
            count.decrementAndGet();
            return count;
        })).get();
    }

    public void clear() {
        feedbackLikes.clear();
    }

}
