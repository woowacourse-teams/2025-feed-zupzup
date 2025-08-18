package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.organization.domain.Organization;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "단체 수정 응답")
public record AdminUpdateOrganizationResponse(
        @Schema(description = "수정된 단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        String organizationUuid,

        @Schema(description = "수정된 단체 이름", example = "우아한테크코스")
        String updateName,

        @Schema(description = "수정된 카테고리", example = "[\"건의\", \"신고\"]")
        Set<String> updateCategories
) {

    public static AdminUpdateOrganizationResponse from(final Organization organization) {
        return new AdminUpdateOrganizationResponse(
                organization.getUuid().toString(),
                organization.getName().getValue(),
                convertCategories(organization.getOrganizationCategories().getActiveCategories())
        );
    }

    private static Set<String> convertCategories(
            final Set<OrganizationCategory> organizationCategories
    ) {
        return organizationCategories.stream()
                .map(result -> result.getCategory().getKoreanName())
                .collect(Collectors.toSet());
    }
}
