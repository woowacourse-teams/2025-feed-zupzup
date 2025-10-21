package feedzupzup.backend.organization.domain;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationCategories {

    @OneToMany(mappedBy = "organization", cascade = CascadeType.PERSIST)
    private final List<OrganizationCategory> organizationCategories = new ArrayList<>();

    public void addAll(final List<String> categories, final Organization organization) {
        final List<OrganizationCategory> mapToCategories = mapToOrganizationCategories(
                categories, organization);
        this.organizationCategories.addAll(mapToCategories);
    }

    private List<OrganizationCategory> mapToOrganizationCategories(
            final List<String> categories,
            final Organization organization
    ) {
        validateCategories(categories);
        return Arrays.stream(Category.values())
                .map(category ->
                        new OrganizationCategory(organization, category,
                                categories.contains(category.getKoreanName())))
                .toList();
    }

    private void validateCategories(final List<String> categories) {
        for (String category : categories) {
            if (!Category.hasCategory(category)) {
                throw new ResourceNotFoundException("category " + category + " 는 존재하지 않는 카테고리입니다.");
            }
        }
    }

    public void updateOrganizationCategories(final List<String> categories) {
        for (OrganizationCategory organizationCategory : this.organizationCategories) {
            if (categories.contains(organizationCategory.getCategory().getKoreanName())) {
                organizationCategory.updateStatus(true);
                continue;
            }
            organizationCategory.updateStatus(false);
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

    public List<OrganizationCategory> getOrganizationCategories() {
        return Collections.unmodifiableList(organizationCategories);
    }

    public List<OrganizationCategory> getActiveCategories() {
        return organizationCategories.stream()
                .filter(OrganizationCategory::isActive)
                .toList();
    }
}
