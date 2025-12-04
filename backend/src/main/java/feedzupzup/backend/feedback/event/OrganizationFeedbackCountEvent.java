package feedzupzup.backend.feedback.event;

import java.util.UUID;

public record OrganizationFeedbackCountEvent(
        UUID organizationUuid
) {

}
