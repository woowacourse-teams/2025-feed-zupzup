package feedzupzup.backend.organization.event;

import java.util.UUID;

public record OrganizationCreatedEvent(
        UUID organizationUuid
) {

}
