package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.organization.domain.Organization;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "단체 조회 응답")
public record UserOrganizationResponse(
        @Schema(description = "단체 이름", example = "우아한테크코스")
        String organizationName
) {
    public static UserOrganizationResponse from(final Organization organization) {
        return new UserOrganizationResponse(organization.getName());
    }
}
