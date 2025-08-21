package feedzupzup.backend.s3.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.global.exception.BusinessViolationException.NotSupportedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class S3ObjectTypeTest {

    @Nested
    @DisplayName("지원하지 않는 확장자 예외 테스트")
    class UnsupportedExtensionTest {

        @Test
        @DisplayName("빈 문자열 확장자로 요청 시 NotSupportedException 발생")
        void throwsNotSupportedExceptionForEmptyExtension() {
            // given
            String emptyExtension = "";

            // when & then
            assertThatThrownBy(() -> S3ObjectType.fromExtension(emptyExtension))
                    .isInstanceOf(NotSupportedException.class)
                    .hasMessage("지원하지 않는 확장자입니다. " + emptyExtension);
        }

        @Test
        @DisplayName("null 확장자로 요청 시 NullPointerException 발생")
        void throwsNullPointerExceptionForNullExtension() {
            // when & then
            assertThatThrownBy(() -> S3ObjectType.fromExtension(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("지원하지 않는 확장자 목록으로 요청 시 NotSupportedException 발생")
        void throwsNotSupportedExceptionForVariousUnsupportedExtensions() {
            // given
            String[] unsupportedExtensions = {"doc", "pdf", "xlsx", "mp4", "avi", "mp3", "wav", "zip", "rar"};

            // when & then
            for (String extension : unsupportedExtensions) {
                assertThatThrownBy(() -> S3ObjectType.fromExtension(extension))
                        .isInstanceOf(NotSupportedException.class)
                        .hasMessage("지원하지 않는 확장자입니다. " + extension);
            }
        }
    }
}
