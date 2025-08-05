package feedzupzup.backend.category.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.CheeringCount;
import feedzupzup.backend.organization.domain.Organization;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AvailableCategoriesTest {

    private Organization organization = new Organization("조직1", new CheeringCount(0));
    private Category category1 = new Category("시설");
    private Category category2 = new Category("문의");

    private AvailableCategory availableCategory1 = new AvailableCategory(organization, category1);
    private AvailableCategory availableCategory2 = new AvailableCategory(organization, category2);

    private final Set<AvailableCategory> categories = Set.of(
            availableCategory1,
            availableCategory2
    );

    @Test
    @DisplayName("String 값으로 카테고리가 주어졌을 때, 검증할 수 있는지 확인한다.")
    void given_category() {
        // given
        AvailableCategories availableCategories = new AvailableCategories(categories);

        //when & then
        assertThat(availableCategories.contains("시설")).isTrue();
        assertThat(availableCategories.contains("흠흠")).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 찾을 경우, 예외가 발생해야 한다.")
    void find_category() {
        // given
        AvailableCategories availableCategories = new AvailableCategories(categories);

        //when & then
        assertThat(availableCategories.findCategoryBy("시설").getContent()).isEqualTo("시설");
        assertThatThrownBy(() -> availableCategories.findCategoryBy("오류"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
