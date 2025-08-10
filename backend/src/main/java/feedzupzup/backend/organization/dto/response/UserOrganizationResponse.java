package feedzupzup.backend.organization.dto.response;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.organization.domain.Organization;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "단체 조회 응답")
public record UserOrganizationResponse(
        @Schema(description = "단체 이름", example = "우아한테크코스")
        String organizationName,
        @Schema(description = "응원 총 횟수", example = "10")
        int totalCheeringCount,
        @Schema(description = "카테고리 리스트", example = "[\"시설\", \"행정\", \"커리큘럼\"]")
        Set<String> categories
        ) {
    public static UserOrganizationResponse from(final Organization organization) {
        return new UserOrganizationResponse(
                organization.getName().getValue(),
                organization.getCheeringCountValue(),
                convertCategories(organization.getOrganizationCategories())
        );
    }

    private static Set<String> convertCategories(final Set<OrganizationCategory> organizationCategories) {
        return organizationCategories.stream()
                .map(result -> result.getCategory().getKoreanName())
                .collect(Collectors.toSet());
    }
}
