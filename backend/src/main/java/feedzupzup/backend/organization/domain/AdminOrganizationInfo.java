package feedzupzup.backend.organization.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminOrganizationInfo(
        UUID organizationUuid,
        String organizationName,
        long waitingCount,
        LocalDateTime postedAt
) {

}
