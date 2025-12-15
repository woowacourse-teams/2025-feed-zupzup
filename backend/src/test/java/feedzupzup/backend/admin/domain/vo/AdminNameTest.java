package feedzupzup.backend.admin.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.admin.domain.exception.AdminException.InvalidAdminNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AdminNameTest {

    @Test
    @DisplayName("유효한 이름으로 AdminName 생성 시 성공한다")
    void adminName_ValidName_Success() {
        // Given
        String validName = "홍길동123";

        // When
        AdminName adminName = new AdminName(validName);

        // Then
        assertThat(adminName.value()).isEqualTo(validName);
    }

    @Test
    @DisplayName("영어로만 이루어진 이름으로 AdminName 생성 시 성공한다")
    void adminName_EnglishName_Success() {
        // Given
        String englishName = "JohnDoe";

        // When
        AdminName adminName = new AdminName(englishName);

        // Then
        assertThat(adminName.value()).isEqualTo(englishName);
    }

    @Test
    @DisplayName("한글로만 이루어진 이름으로 AdminName 생성 시 성공한다")
    void adminName_KoreanName_Success() {
        // Given
        String koreanName = "홍길동";

        // When
        AdminName adminName = new AdminName(koreanName);

        // Then
        assertThat(adminName.value()).isEqualTo(koreanName);
    }

    @Test
    @DisplayName("숫자로만 이루어진 이름으로 AdminName 생성 시 성공한다")
    void adminName_NumberName_Success() {
        // Given
        String numberName = "12345";

        // When
        AdminName adminName = new AdminName(numberName);

        // Then
        assertThat(adminName.value()).isEqualTo(numberName);
    }

    @Test
    @DisplayName("빈 문자열로 AdminName 생성 시 InvalidAdminNameException이 발생한다")
    void adminName_EmptyName_ThrowsInvalidAdminNameException() {
        // Given
        String emptyName = "";

        // When & Then
        assertThatThrownBy(() -> new AdminName(emptyName))
                .isInstanceOf(InvalidAdminNameException.class)
                .hasMessageContaining("adminName = " + emptyName + " length = 0");
    }

    @Test
    @DisplayName("20자를 초과하는 이름으로 AdminName 생성 시 InvalidAdminNameException이 발생한다")
    void adminName_TooLongName_ThrowsInvalidAdminNameException() {
        // Given
        String tooLongName = "a".repeat(21);

        // When & Then
        assertThatThrownBy(() -> new AdminName(tooLongName))
                .isInstanceOf(InvalidAdminNameException.class)
                .hasMessageContaining("adminName = " + tooLongName + " length = 21");
    }

    @Test
    @DisplayName("20자 이름으로 AdminName 생성 시 성공한다")
    void adminName_MaxLengthName_Success() {
        // Given
        String maxLengthName = "a".repeat(20);

        // When
        AdminName adminName = new AdminName(maxLengthName);

        // Then
        assertThat(adminName.value()).isEqualTo(maxLengthName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin name", "admin!!", "admin@123", "홍길동!", "test#user", "user$name"})
    @DisplayName("특수문자가 포함된 이름으로 AdminName 생성 시 InvalidAdminNameException이 발생한다")
    void adminName_InvalidFormat_ThrowsInvalidAdminNameException(String invalidName) {
        // When & Then
        assertThatThrownBy(() -> new AdminName(invalidName))
                .isInstanceOf(InvalidAdminNameException.class)
                .hasMessageContaining("은(는) 영어, 숫자, 한글만 포함해야 합니다.");
    }

    @Test
    @DisplayName("공백이 포함된 이름으로 AdminName 생성 시 InvalidAdminNameException이 발생한다")
    void adminName_NameWithSpace_ThrowsInvalidAdminNameException() {
        // Given
        String nameWithSpace = "홍 길동";

        // When & Then
        assertThatThrownBy(() -> new AdminName(nameWithSpace))
                .isInstanceOf(InvalidAdminNameException.class)
                .hasMessageContaining("은(는) 영어, 숫자, 한글만 포함해야 합니다.");
    }

    @Test
    @DisplayName("특수문자만으로 이루어진 이름으로 AdminName 생성 시 InvalidAdminNameException이 발생한다")
    void adminName_OnlySpecialCharacters_ThrowsInvalidAdminNameException() {
        // Given
        String specialCharName = "!@#$%";

        // When & Then
        assertThatThrownBy(() -> new AdminName(specialCharName))
                .isInstanceOf(InvalidAdminNameException.class)
                .hasMessageContaining("은(는) 영어, 숫자, 한글만 포함해야 합니다.");
    }
}