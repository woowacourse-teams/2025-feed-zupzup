package feedzupzup.backend.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class BCryptPasswordEncoderAdapterTest {

    private BCryptPasswordEncoderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BCryptPasswordEncoderAdapter();
    }

    @Test
    @DisplayName("비밀번호를 인코딩하면 BCrypt 형식의 문자열이 반환된다")
    void encode_ReturnsBCryptFormat() {
        // given
        Password password = new Password("password123");

        // when
        EncodedPassword encoded = adapter.encode(password);

        // then
        assertThat(encoded.value()).startsWith("$2a$");
        assertThat(encoded.value()).hasSize(60);  // BCrypt는 항상 60자
    }

    @Test
    @DisplayName("일치하는 비밀번호는 true를 반환한다")
    void matches_CorrectPassword_ReturnsTrue() {
        // given
        Password password = new Password("password123");
        EncodedPassword encoded = adapter.encode(password);

        // when
        boolean result = adapter.matches("password123", encoded.value());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("일치하지 않는 비밀번호는 false를 반환한다")
    void matches_WrongPassword_ReturnsFalse() {
        // given
        Password password = new Password("password123");
        EncodedPassword encoded = adapter.encode(password);

        // when
        boolean result = adapter.matches("wrongPassword", encoded.value());

        // then
        assertThat(result).isFalse();
    }
}
