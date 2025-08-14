package feedzupzup.backend.organization.domain;

import java.time.LocalDateTime;

public record AdminOrganizationInfo(
        String organizationName,
        long waitingCount,
        LocalDateTime postedAt
) {

}
