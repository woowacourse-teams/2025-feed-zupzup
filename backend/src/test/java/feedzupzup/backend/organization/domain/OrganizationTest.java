package feedzupzup.backend.organization.domain;

import static org.assertj.core.api.Assertions.*;

import feedzupzup.backend.organization.domain.OrganizationException.OrganizationNumberException;
import feedzupzup.backend.organization.domain.vo.CheeringCount;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrganizationTest {

    @DisplayName("요청한 응원 횟수만큼 단체의 총 응원횟수가 증가한다.")
    @Test
    void cheer_test() {
        // given
        final int originCount = 100;
        final Organization organization = OrganizationFixture.create(originCount);

        final int updateCount = 100;
        final CheeringCount updateCheeringCount = new CheeringCount(updateCount);

        // when
        organization.cheer(updateCheeringCount);

        // then
        assertThat(organization.getCheeringCountValue()).isEqualTo(originCount + updateCount);
    }

    @DisplayName("요청 횟수가 한 번에 100을 넘어갈 경우, 예외를 던져야 한다.")
    @Test
    void cheer_fail_test() {
        // given
        final int originCount = 100;
        final Organization organization = OrganizationFixture.create(originCount);

        final int updateCount = 150;
        final CheeringCount updateCheeringCount = new CheeringCount(updateCount);

        // when & then
        assertThatThrownBy(() -> organization.cheer(updateCheeringCount))
                .isInstanceOf(OrganizationNumberException.class);

    }
}
