package feedzupzup.backend.feedback.domain.event;

import org.springframework.context.ApplicationEvent;

public record FeedbackCreatedEvent2 (
        Long feedbackId
){
}
