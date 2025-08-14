package feedzupzup.backend.organization.domain;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class OrganizationCategories {

    private final Set<OrganizationCategory> organizationCategories;

    private OrganizationCategories(final Set<String> categories, final Organization organization) {
        this.organizationCategories = new HashSet<>(convertOf(categories, organization));
    }

    public static OrganizationCategories createAndConvert(final Set<String> categories, final Organization organization) {
        return new OrganizationCategories(categories, organization);
    }

    private Set<OrganizationCategory> convertOf(final Set<String> categories, final Organization organization) {
        return categories.stream()
                .map(categoryName ->
                        new OrganizationCategory(organization, Category.findCategoryBy(categoryName)))
                .collect(Collectors.toSet());
    }

    public Set<OrganizationCategory> getOrganizationCategories() {
        return Collections.unmodifiableSet(organizationCategories);
    }
}
