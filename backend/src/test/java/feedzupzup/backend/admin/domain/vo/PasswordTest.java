package feedzupzup.backend.admin.domain.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.auth.encoder.PasswordEncoder;
import feedzupzup.backend.auth.exception.AuthException;
import feedzupzup.backend.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@Import(PasswordEncoder.class)
class PasswordTest {

    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("짧은 비밀번호로 Password 생성 시 AuthException이 발생한다")
    void password_ShortPassword_ThrowsAuthException() {
        // Given
        String shortPassword = "test";

        // When & Then
        assertThatThrownBy(() -> Password.createEncodedPassword(shortPassword, passwordEncoder))
                .isInstanceOf(AuthException.class)
                .hasMessage("password = test length = 4")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PASSWORD_FORMAT);
    }
}
