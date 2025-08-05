package feedzupzup.backend.category.domain;

import static feedzupzup.backend.category.domain.Category.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.CheeringCount;
import feedzupzup.backend.organization.domain.Organization;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrganizationCategoriesTest {

    private Organization organization = new Organization("조직1", new CheeringCount(0));
    private OrganizationCategory organizationCategory1 = new OrganizationCategory(organization, FACILITY);
    private OrganizationCategory organizationCategory2 = new OrganizationCategory(organization, CURRICULUM);

    private final Set<OrganizationCategory> categories = Set.of(
            organizationCategory1,
            organizationCategory2
    );

    @Test
    @DisplayName("존재하지 않는 카테고리를 찾을 경우, 예외가 발생해야 한다.")
    void find_category() {
        // given
        OrganizationCategories organizationCategories = new OrganizationCategories(categories);

        //when & then
        assertThat(organizationCategories.findOrganizationCategoryBy(FACILITY).getCategory()).isEqualTo(FACILITY);
        assertThatThrownBy(() -> organizationCategories.findOrganizationCategoryBy(ETC))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
