package feedzupzup.backend.organization.dto.request;

import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.vo.CheeringCount;
import feedzupzup.backend.organization.domain.vo.Name;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Schema(description = "단체 저장 요청")
public record CreateOrganizationRequest(
        @Schema(description = "단체 이름", example = "우아한테크코스")
        String organizationName,

        @Schema(description = "카테고리 리스트", example = "[\"건의\", \"신고\"]")
        List<String> categories
) {

    public Organization toOrganization() {
        return new Organization(UUID.randomUUID(), new Name(organizationName), new CheeringCount(0));
    }

}
