package feedzupzup.backend.category.fixture;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.organization.domain.Organization;

public class OrganizationCategoryFixture {

    public static OrganizationCategory createOrganizationCategory(
            final Organization organization,
            final Category category) {
        return new OrganizationCategory(organization, category);
    }

    public static OrganizationCategory createOrganizationCategory(Organization organization) {
        return new OrganizationCategory(organization, Category.ETC);
    }

}
