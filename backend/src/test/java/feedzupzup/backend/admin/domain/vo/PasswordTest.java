package feedzupzup.backend.admin.domain.vo;

import feedzupzup.backend.auth.exception.AuthException;
import feedzupzup.backend.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordTest {

    @Test
    @DisplayName("짧은 비밀번호로 Password 생성 시 AuthException이 발생한다")
    void password_ShortPassword_ThrowsAuthException() {
        // Given
        String shortPassword = "test";

        // When & Then
        assertThatThrownBy(() -> new Password(shortPassword))
                .isInstanceOf(AuthException.class)
                .hasMessage("password = test length = 4")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PASSWORD_FORMAT);
    }
}