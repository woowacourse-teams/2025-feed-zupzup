package feedzupzup.backend.category.domain;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Getter
public class OrganizationCategories {

    private final Set<OrganizationCategory> organizationCategories;

    public OrganizationCategories(final Set<OrganizationCategory> organizationCategories) {
        this.organizationCategories = new HashSet<>(organizationCategories);
    }

    public OrganizationCategory findOrganizationCategoryBy(final Category category) {
        return organizationCategories.stream()
                .filter(result -> result.getCategory().equals(category))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));
    }
}
