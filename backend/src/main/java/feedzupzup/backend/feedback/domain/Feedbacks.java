package feedzupzup.backend.feedback.domain;

import java.util.ArrayList;
import java.util.List;

public class Feedbacks {

    private final List<Feedback> feedbacks;

    public Feedbacks(final List<Feedback> feedbacks) {
        this.feedbacks = new ArrayList<>(feedbacks);
    }

    public double calculateReflectionRate() {
        final double value = (double) calculateConfirmedCount() / feedbacks.size() * 100.0;
        return Math.round(value * 100.0) / 100.0;
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
