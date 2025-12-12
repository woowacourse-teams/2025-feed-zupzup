package feedzupzup.backend.feedback.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OutboxCreatedEvent {

    private final Long feedbackId;

    public static OutboxCreatedEvent of(final Long feedbackId) {
        return new OutboxCreatedEvent(feedbackId);
    }
}
