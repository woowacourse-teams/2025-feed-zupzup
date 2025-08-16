package feedzupzup.backend.feedback.event;

public record FeedbackCreatedEvent(
        Long organizationId,
        String title
) {
}