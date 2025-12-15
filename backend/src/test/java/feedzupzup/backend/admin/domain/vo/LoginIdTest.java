package feedzupzup.backend.admin.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.admin.domain.exception.AdminException.InvalidAdminIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LoginIdTest {

    @Test
    @DisplayName("유효한 loginId로 LoginId 생성 시 성공한다")
    void loginId_ValidId_Success() {
        // Given
        String validId = "admin123";

        // When
        LoginId loginId = new LoginId(validId);

        // Then
        assertThat(loginId.value()).isEqualTo(validId);
    }

    @Test
    @DisplayName("영어로만 이루어진 loginId로 LoginId 생성 시 성공한다")
    void loginId_EnglishOnly_Success() {
        // Given
        String englishId = "adminuser";

        // When
        LoginId loginId = new LoginId(englishId);

        // Then
        assertThat(loginId.value()).isEqualTo(englishId);
    }

    @Test
    @DisplayName("숫자로만 이루어진 loginId로 LoginId 생성 시 성공한다")
    void loginId_NumberOnly_Success() {
        // Given
        String numberId = "12345";

        // When
        LoginId loginId = new LoginId(numberId);

        // Then
        assertThat(loginId.value()).isEqualTo(numberId);
    }

    @Test
    @DisplayName("5자 길이의 loginId로 LoginId 생성 시 성공한다")
    void loginId_MinLength_Success() {
        // Given
        String minLengthId = "admin";

        // When
        LoginId loginId = new LoginId(minLengthId);

        // Then
        assertThat(loginId.value()).isEqualTo(minLengthId);
    }

    @Test
    @DisplayName("20자 길이의 loginId로 LoginId 생성 시 성공한다")
    void loginId_MaxLength_Success() {
        // Given
        String maxLengthId = "a".repeat(20);

        // When
        LoginId loginId = new LoginId(maxLengthId);

        // Then
        assertThat(loginId.value()).isEqualTo(maxLengthId);
    }

    @Test
    @DisplayName("4자 이하의 loginId로 LoginId 생성 시 InvalidAdminIdException이 발생한다")
    void loginId_TooShort_ThrowsInvalidAdminIdException() {
        // Given
        String shortId = "test";

        // When & Then
        assertThatThrownBy(() -> new LoginId(shortId))
                .isInstanceOf(InvalidAdminIdException.class)
                .hasMessageContaining("loginId = " + shortId + " length = 4");
    }

    @Test
    @DisplayName("21자 이상의 loginId로 LoginId 생성 시 InvalidAdminIdException이 발생한다")
    void loginId_TooLong_ThrowsInvalidAdminIdException() {
        // Given
        String longId = "a".repeat(21);

        // When & Then
        assertThatThrownBy(() -> new LoginId(longId))
                .isInstanceOf(InvalidAdminIdException.class)
                .hasMessageContaining("loginId = " + longId + " length = 21");
    }

    @Test
    @DisplayName("공백이 포함된 loginId로 LoginId 생성 시 InvalidAdminIdException이 발생한다")
    void loginId_ContainsSpace_ThrowsInvalidAdminIdException() {
        // Given
        String idWithSpace = "admin user";

        // When & Then
        assertThatThrownBy(() -> new LoginId(idWithSpace))
                .isInstanceOf(InvalidAdminIdException.class)
                .hasMessageContaining("공백이 포함 되어있습니다.");
    }

    @Test
    @DisplayName("앞에 공백이 포함된 loginId로 LoginId 생성 시 InvalidAdminIdException이 발생한다")
    void loginId_StartsWithSpace_ThrowsInvalidAdminIdException() {
        // Given
        String idWithSpace = " admin";

        // When & Then
        assertThatThrownBy(() -> new LoginId(idWithSpace))
                .isInstanceOf(InvalidAdminIdException.class)
                .hasMessageContaining("공백이 포함 되어있습니다.");
    }

    @Test
    @DisplayName("뒤에 공백이 포함된 loginId로 LoginId 생성 시 InvalidAdminIdException이 발생한다")
    void loginId_EndsWithSpace_ThrowsInvalidAdminIdException() {
        // Given
        String idWithSpace = "admin ";

        // When & Then
        assertThatThrownBy(() -> new LoginId(idWithSpace))
                .isInstanceOf(InvalidAdminIdException.class)
                .hasMessageContaining("공백이 포함 되어있습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin!", "user@123", "test#id", "login$", "admin%user"})
    @DisplayName("특수문자가 포함된 loginId로 LoginId 생성 시 InvalidAdminIdException이 발생한다")
    void loginId_ContainsSpecialCharacter_ThrowsInvalidAdminIdException(String invalidId) {
        // When & Then
        assertThatThrownBy(() -> new LoginId(invalidId))
                .isInstanceOf(InvalidAdminIdException.class)
                .hasMessageContaining("은(는) 영문과 숫자만 포함해야 합니다.");
    }

    @Test
    @DisplayName("한글이 포함된 loginId로 LoginId 생성 시 InvalidAdminIdException이 발생한다")
    void loginId_ContainsKorean_ThrowsInvalidAdminIdException() {
        // Given
        String koreanId = "admin관리자";

        // When & Then
        assertThatThrownBy(() -> new LoginId(koreanId))
                .isInstanceOf(InvalidAdminIdException.class)
                .hasMessageContaining("은(는) 영문과 숫자만 포함해야 합니다.");
    }

    @Test
    @DisplayName("특수문자만으로 이루어진 loginId로 LoginId 생성 시 InvalidAdminIdException이 발생한다")
    void loginId_OnlySpecialCharacters_ThrowsInvalidAdminIdException() {
        // Given
        String specialCharId = "!@#$%^";

        // When & Then
        assertThatThrownBy(() -> new LoginId(specialCharId))
                .isInstanceOf(InvalidAdminIdException.class)
                .hasMessageContaining("은(는) 영문과 숫자만 포함해야 합니다.");
    }
}