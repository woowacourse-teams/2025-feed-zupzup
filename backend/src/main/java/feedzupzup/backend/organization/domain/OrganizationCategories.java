package feedzupzup.backend.organization.domain;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationCategories {

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<OrganizationCategory> organizationCategories = new HashSet<>();

    public void addAll(final Set<String> categories, final Organization organization) {
        final Set<OrganizationCategory> mapToCategories = mapToOrganizationCategories(
                categories, organization);
        this.organizationCategories.addAll(mapToCategories);
    }

    private Set<OrganizationCategory> mapToOrganizationCategories(
            final Set<String> categories,
            final Organization organization
    ) {
        validateCategories(categories);
        return Arrays.stream(Category.values())
                .map(category ->
                        new OrganizationCategory(organization, category,
                                categories.contains(category.getKoreanName())))
                .collect(Collectors.toSet());
    }

    private void validateCategories(final Set<String> categories) {
        for (String category : categories) {
            if (!Category.hasCategory(category)) {
                throw new ResourceNotFoundException("category " + category + " 는 존재하지 않는 카테고리입니다.");
            }
        }
    }

    public void updateOrganizationCategories(final Set<String> categories) {
        for (OrganizationCategory organizationCategory : this.organizationCategories) {
            if (categories.contains(organizationCategory.getCategory().getKoreanName())) {
                organizationCategory.modifyUpdateStatus(true);
                continue;
            }
            organizationCategory.modifyUpdateStatus(false);
        }
    }

    public OrganizationCategory findOrganizationCategoryBy(final Category category) {
        final OrganizationCategory resultCategory = organizationCategories.stream()
                .filter(organizationCategory -> organizationCategory.isSameCategory(category))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));

        if (!resultCategory.isActive()) {
            throw new ResourceNotFoundException("해당 카테고리는 현재 비활성화 되어있습니다.");
        }
        return resultCategory;
    }

    public Set<OrganizationCategory> getOrganizationCategories() {
        return Collections.unmodifiableSet(organizationCategories);
    }

    public Set<OrganizationCategory> getActiveCategories() {
        return organizationCategories.stream()
                .filter(OrganizationCategory::isActive)
                .collect(Collectors.toUnmodifiableSet());
    }
}
