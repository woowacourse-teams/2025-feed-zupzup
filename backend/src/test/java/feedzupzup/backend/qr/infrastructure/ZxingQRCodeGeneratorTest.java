package feedzupzup.backend.qr.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZxingQRCodeGeneratorTest {

    @InjectMocks
    private ZxingQRCodeGenerator qrImageGenerator;

    @Nested
    @DisplayName("QR 이미지 생성 테스트")
    class GenerateQRImageTest {

        @Test
        @DisplayName("유효한 URL로 QR 이미지를 성공적으로 생성한다")
        void generateQRImage_success() throws IOException {
            // given
            final String url = "https://feedzupzup.com/dashboard?uuid=123e4567-e89b-12d3-a456-426614174000";

            // when
            final byte[] result = qrImageGenerator.generateQRCode(url);

            // then
            assertThat(result).isNotNull();
            assertThat(result.length).isGreaterThan(0);

            // 생성된 이미지가 올바른 형식인지 확인
            final BufferedImage image = ImageIO.read(new ByteArrayInputStream(result));
            assertThat(image).isNotNull();
            assertThat(image.getWidth()).isEqualTo(ZxingQRCodeGenerator.IMAGE_WIDTH_PIXELS);
            assertThat(image.getHeight()).isEqualTo(ZxingQRCodeGenerator.IMAGE_HEIGHT_PIXELS);
        }

        @Test
        @DisplayName("생성된 QR 이미지가 올바른 크기를 가진다")
        void generateQRImage_has_correct_dimensions() throws IOException {
            // given
            final String url = "https://example.com";

            // when
            final byte[] result = qrImageGenerator.generateQRCode(url);

            // then
            final BufferedImage image = ImageIO.read(new ByteArrayInputStream(result));
            assertThat(image.getWidth()).isEqualTo(300);
            assertThat(image.getHeight()).isEqualTo(300);
        }

        @Test
        @DisplayName("여러 번 호출해도 일관된 결과를 반환한다")
        void generateQRImage_consistent_results() {
            // given
            final String url = "https://feedzupzup.com";

            // when
            final byte[] result1 = qrImageGenerator.generateQRCode(url);
            final byte[] result2 = qrImageGenerator.generateQRCode(url);

            // then
            assertThat(result1).isEqualTo(result2);
        }
    }
}