package feedzupzup.backend.feedback.domain.event;

public record VoyageRetryOutboxCreatedEvent(Long feedbackId) {

    public static VoyageRetryOutboxCreatedEvent of(final Long feedbackId) {
        return new VoyageRetryOutboxCreatedEvent(feedbackId);
    }
}
