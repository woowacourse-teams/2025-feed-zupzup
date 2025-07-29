package feedzupzup.backend.organization.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrganizationTest {

    @DisplayName("요청한 응원 횟수만큼 단체의 총 응원횟수가 증가한다.")
    @Test
    void cheer_test() {
        // given
        int originCount = 100;
        Organization organization = Organization.builder()
                .name("우아한테크코스")
                .cheeringCount(new CheeringCount(originCount))
                .build();

        int updateCount = 150;
        CheeringCount updateCheeringCount = new CheeringCount(updateCount);

        // when
        organization.cheer(updateCheeringCount);

        // then
        Assertions.assertThat(organization.getCheeringCount()).isEqualTo(new CheeringCount(originCount + updateCount));
    }
}
