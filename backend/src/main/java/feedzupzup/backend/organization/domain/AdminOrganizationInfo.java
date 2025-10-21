package feedzupzup.backend.organization.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminOrganizationInfo(
        UUID organizationUuid,
        String organizationName,
        long confirmedCount,
        long waitingCount,
        LocalDateTime postedAt
) {

}
