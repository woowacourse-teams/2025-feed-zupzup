package feedzupzup.backend.category.fixture;

import feedzupzup.backend.category.domain.AvailableCategory;
import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.organization.domain.Organization;

public class CategoryFixture {

    public static Category createCategoryBy(String categoryValue) {
        return new Category(categoryValue);
    }

    public static AvailableCategory createAvailableCategory(
            final Organization organization,
            final Category category) {
        return new AvailableCategory(organization, category);
    }

}
