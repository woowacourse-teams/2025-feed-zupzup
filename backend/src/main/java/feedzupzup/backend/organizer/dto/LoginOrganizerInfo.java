package feedzupzup.backend.organizer.dto;

import java.util.UUID;

public record LoginOrganizerInfo(
        Long adminId,
        UUID organizationUuid
) {

}
