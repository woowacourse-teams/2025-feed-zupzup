package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.organization.domain.Organization;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "조직 생성 응답")
public record AdminCreateOrganizationResponse(
        @Schema(description = "생성된 조직 ID", example = "1")
        Long organizationId
) {

    public static AdminCreateOrganizationResponse from(final Organization organization) {
        return new AdminCreateOrganizationResponse(organization.getId());
    }
}
