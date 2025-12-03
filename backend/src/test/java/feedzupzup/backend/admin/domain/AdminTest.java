package feedzupzup.backend.admin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminTest {

    @Mock
    private LoginId loginId;

    @Mock
    private EncodedPassword encodedPassword;

    @Mock
    private AdminName adminName;

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

    @DisplayName("로그인 id가 null이면 예외가 발생한다.")
    @Test
    void loginId_null_throwsException() {
        //when & then
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> new Admin(null, encodedPassword, adminName));
    }

    @DisplayName("패스워드가 null이면 예외가 발생한다.")
    @Test
    void password_null_throwsException() {
        //when & then
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> new Admin(loginId, null, adminName));
    }

    @DisplayName("관리자 이름이 null이면 예외가 발생한다.")
    @Test
    void name_null_throwsException() {
        //when & then
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> new Admin(loginId, encodedPassword, null));
    }
}
