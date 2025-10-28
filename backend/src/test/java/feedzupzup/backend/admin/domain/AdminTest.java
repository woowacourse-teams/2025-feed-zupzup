package feedzupzup.backend.admin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdminTest {

    @DisplayName("관리자의 비밀번호를 변경할 수 있다")
    @Test
    void changePassword_success() {
        // given
        Admin admin = AdminFixture.create();
        String originalPassword = admin.getPasswordValue();
        EncodedPassword newPassword = new EncodedPassword("newPassword456");

        // when
        admin.changePassword(newPassword);

        // then
        assertThat(admin.getPasswordValue()).isEqualTo("newPassword456");
        assertThat(admin.getPasswordValue()).isNotEqualTo(originalPassword);
    }
}