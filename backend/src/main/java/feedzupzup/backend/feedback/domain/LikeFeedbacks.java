package feedzupzup.backend.feedback.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LikeFeedbacks {

    private final Set<Long> likeFeedbacks = new HashSet<>();

    public void add(final long value) {
        likeFeedbacks.add(value);
    }

    public boolean hasFeedbackId(final long value) {
        return likeFeedbacks.contains(value);
    }

    public Set<Long> getLikeFeedbacks() {
        return Collections.unmodifiableSet(likeFeedbacks);
    }
}
