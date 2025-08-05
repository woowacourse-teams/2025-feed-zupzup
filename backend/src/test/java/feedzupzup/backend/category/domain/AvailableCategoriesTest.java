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

class AvailableCategoriesTest {

    private Organization organization = new Organization("조직1", new CheeringCount(0));
    private AvailableCategory availableCategory1 = new AvailableCategory(organization, FACILITY);
    private AvailableCategory availableCategory2 = new AvailableCategory(organization, CURRICULUM);

    private final Set<AvailableCategory> categories = Set.of(
            availableCategory1,
            availableCategory2
    );

    @Test
    @DisplayName("존재하지 않는 카테고리를 찾을 경우, 예외가 발생해야 한다.")
    void find_category() {
        // given
        AvailableCategories availableCategories = new AvailableCategories(categories);

        //when & then
        assertThat(availableCategories.findAvailableCategoryBy(FACILITY).getCategory()).isEqualTo(FACILITY);
        assertThatThrownBy(() -> availableCategories.findAvailableCategoryBy(ETC))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
