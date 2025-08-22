package feedzupzup.backend.organization.fixture;

import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.vo.CheeringCount;
import feedzupzup.backend.organization.domain.vo.Name;
import java.util.UUID;

public class OrganizationFixture {

    public static Organization createAllBlackBox() {
        return Organization.builder()
                .uuid(UUID.randomUUID())
                .name(new Name("테스트장소"))
                .cheeringCount(new CheeringCount(0))
                .build();
    }

    public static Organization createByName(final String name) {
        return Organization.builder()
                .uuid(UUID.randomUUID())
                .name(new Name(name))
                .cheeringCount(new CheeringCount(0))
                .build();
    }

    public static Organization create(final int originCount) {
        return Organization.builder()
                .uuid(UUID.randomUUID())
                .name(new Name("테스트장소"))
                .cheeringCount(new CheeringCount(originCount))
                .build();
    }
}
