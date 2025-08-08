package feedzupzup.backend.feedback.domain;

public record FeedbackAmount(
        long totalCount,
        long confirmedCount,
        long waitingCount
) {

    public int calculateReflectionRate() {
        if (totalCount == 0) {
            return 0;
        }
        final double value = (double) confirmedCount / totalCount * 100.0;
        return (int) Math.round(value);
    }

}