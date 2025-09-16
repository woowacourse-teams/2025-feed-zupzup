package feedzupzup.backend.feedback.domain;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LikeFeedbacks {

    private final Set<Long> likeFeedbacks = ConcurrentHashMap.newKeySet();

    public void add(final long value) {
        likeFeedbacks.add(value);
    }

    public boolean hasFeedbackId(final long value) {
        return likeFeedbacks.contains(value);
    }

    public void remove(final long value) {
        likeFeedbacks.remove(value);
    }


    public Set<Long> getLikeFeedbacks() {
        return Collections.unmodifiableSet(likeFeedbacks);
    }
}
