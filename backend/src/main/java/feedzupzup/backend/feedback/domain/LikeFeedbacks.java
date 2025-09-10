package feedzupzup.backend.feedback.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LikeFeedbacks {

    private final Set<Integer> likeFeedbacks = new HashSet<>();

    public void add(final int value) {
        likeFeedbacks.add(value);
    }

    public boolean hasFeedbackId(final int value) {
        return likeFeedbacks.contains(value);
    }

    public Set<Integer> getLikeFeedbacks() {
        return Collections.unmodifiableSet(likeFeedbacks);
    }
}
