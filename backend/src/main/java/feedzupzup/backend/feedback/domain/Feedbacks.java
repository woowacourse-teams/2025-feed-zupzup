package feedzupzup.backend.feedback.domain;

import java.util.ArrayList;
import java.util.List;

public class Feedbacks {

    private final List<Feedback> feedbacks;

    public Feedbacks(final List<Feedback> feedbacks) {
        this.feedbacks = new ArrayList<>(feedbacks);
    }

    public int calculateReflectionRate() {
        final double value = (double) calculateConfirmedCount() / feedbacks.size() * 100.0;
        return (int) Math.round(value);
    }

    public int calculateConfirmedCount() {
        return (int) feedbacks.stream()
                .filter(Feedback::isConfirmed)
                .count();
    }

    public int calculateWaitingCount() {
        return (int) feedbacks.stream()
                .filter(Feedback::isWaiting)
                .count();
    }

    public int getSize() {
        return feedbacks.size();
    }
}
