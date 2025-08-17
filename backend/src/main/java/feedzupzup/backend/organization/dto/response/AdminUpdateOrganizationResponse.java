package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.organization.domain.Organization;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "단체 수정 응답")
public record AdminUpdateOrganizationResponse(
        @Schema(description = "수정된 단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        String organizationUuid
) {

    public static AdminUpdateOrganizationResponse from(final Organization organization) {
        return new AdminUpdateOrganizationResponse(organization.getUuid().toString());
    }
}
