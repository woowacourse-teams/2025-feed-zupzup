package feedzupzup.backend.admin.domain.fixture;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;

public class AdminFixture {

    public static Admin create() {
        return new Admin(
                new LoginId("admin123"),
                new Password("password123"),
                new AdminName("lisa"));
    }

}
