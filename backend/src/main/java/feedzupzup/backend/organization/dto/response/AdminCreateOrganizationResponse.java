package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.organization.domain.Organization;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "조직 생성 응답")
public record AdminCreateOrganizationResponse(
        @Schema(description = "생성된 조직 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        String organizationUuid
) {

    public static AdminCreateOrganizationResponse from(final Organization organization) {
        return new AdminCreateOrganizationResponse(organization.getUuid().toString());
    }
}
