package feedzupzup.backend.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CheeringCountTest {

    @DisplayName("음수의 값으로 응원횟수를 생성하면 예외가 발생한다.")
    @Test
    void exception_create_negative_value_cheering_count() {
        // given
        int negativeValue = -1;

        // when & then
        assertThatThrownBy(() -> new CheeringCount(negativeValue))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자신의 응원횟수에 요청한 응원횟수만큼 더할 수 있다.")
    @Test
    void add_cheering_count() {
        // given
        int originValue = 100;
        int updateValue = 50;
        CheeringCount origin = new CheeringCount(originValue);
        CheeringCount update = new CheeringCount(updateValue);

        // when
        origin.add(update);

        // then
        assertThat(origin).isEqualTo(new CheeringCount(originValue + updateValue));
    }
}
