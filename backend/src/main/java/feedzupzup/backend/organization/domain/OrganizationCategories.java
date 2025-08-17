package feedzupzup.backend.organization.domain;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class OrganizationCategories {

    private final Set<OrganizationCategory> organizationCategories;

    private OrganizationCategories(final Set<String> categories, final Organization organization) {
        this.organizationCategories = new HashSet<>(
                mapToOrganizationCategories(categories, organization));
    }

    public static OrganizationCategories createOf(final Set<String> categories, final Organization organization) {
        return new OrganizationCategories(categories, organization);
    }

    private Set<OrganizationCategory> mapToOrganizationCategories(final Set<String> categories, final Organization organization) {
        validateCategories(categories);
        return Arrays.stream(Category.values())
                .map(category ->
                        new OrganizationCategory(organization, category, categories.contains(category.getKoreanName())))
                .collect(Collectors.toSet());
    }

    private void validateCategories(final Set<String> categories) {
        for (String category : categories) {
            if (Category.hasCategory(category)) {
                return;
            }
            throw new ResourceNotFoundException("category " + category + " 는 존재하지 않는 카테고리입니다.");
        }
    }

    public Set<OrganizationCategory> getOrganizationCategories() {
        return Collections.unmodifiableSet(organizationCategories);
    }
}
