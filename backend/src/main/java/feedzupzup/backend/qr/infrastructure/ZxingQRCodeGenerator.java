package feedzupzup.backend.qr.infrastructure;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import feedzupzup.backend.qr.config.QRProperties;
import feedzupzup.backend.qr.infrastructure.exception.QRGenerationException;
import feedzupzup.backend.qr.service.QRCodeGenerator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZxingQRCodeGenerator implements QRCodeGenerator {

    private final QRProperties qrProperties;

    public byte[] generateQRCode(final String url) {
        try {
            final BitMatrix bitMatrix = createBitMatrix(url);
            return convertBitMatrixToByteArray(bitMatrix);
        } catch (final WriterException | IOException e) {
            throw new QRGenerationException();
        }
    }

    private BitMatrix createBitMatrix(final String url) throws WriterException {
        final QRCodeWriter qrCodeWriter = new QRCodeWriter();

        final Map<EncodeHintType, Object> options = new HashMap<>();
        options.put(EncodeHintType.ERROR_CORRECTION,
                ErrorCorrectionLevel.valueOf(qrProperties.generation().errorCorrectionLevel()));
        options.put(EncodeHintType.CHARACTER_SET, qrProperties.generation().characterSet());
        options.put(EncodeHintType.MARGIN, qrProperties.generation().margin());

        return qrCodeWriter.encode(
                url,
                BarcodeFormat.QR_CODE,
                qrProperties.image().width(),
                qrProperties.image().height(),
                options
        );
    }

    private byte[] convertBitMatrixToByteArray(final BitMatrix bitMatrix) throws IOException {
        final BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(qrImage, qrProperties.image().extension(), outputStream);
            return outputStream.toByteArray();
        }
    }
}
