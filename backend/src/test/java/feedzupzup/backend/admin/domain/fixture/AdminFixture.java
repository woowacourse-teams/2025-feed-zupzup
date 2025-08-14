package feedzupzup.backend.admin.domain.fixture;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;

public class AdminFixture {

    public static Admin create() {
        return new Admin(
                new LoginId("admin123"),
                new EncodedPassword("password123"),
                new AdminName("lisa"));
    }

}
