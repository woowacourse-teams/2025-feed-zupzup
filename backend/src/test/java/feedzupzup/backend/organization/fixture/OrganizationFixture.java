package feedzupzup.backend.organization.fixture;

import feedzupzup.backend.organization.domain.vo.CheeringCount;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.vo.Name;

public class OrganizationFixture {

    public static Organization createAllBlackBox() {
        return Organization.builder()
                .name(new Name("테스트장소"))
                .cheeringCount(new CheeringCount(0))
                .build();
    }

    public static Organization create(final int originCount) {
        return Organization.builder()
                .name(new Name("테스트장소"))
                .cheeringCount(new CheeringCount(originCount))
                .build();
    }
}
