package feedzupzup.backend.qr.infrastructure;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import feedzupzup.backend.qr.infrastructure.exception.QRGenerationException;
import feedzupzup.backend.qr.service.QRCodeGenerator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class ZxingQRCodeGenerator implements QRCodeGenerator {

    static final int IMAGE_WIDTH_PIXELS = 300;
    static final int IMAGE_HEIGHT_PIXELS = 300;
    static final String QR_IMAGE_EXTENSION = "PNG";

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
        options.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        options.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        options.put(EncodeHintType.MARGIN, 1);

        return qrCodeWriter.encode(
                url,
                BarcodeFormat.QR_CODE,
                IMAGE_WIDTH_PIXELS,
                IMAGE_HEIGHT_PIXELS,
                options
        );
    }

    private byte[] convertBitMatrixToByteArray(final BitMatrix bitMatrix) throws IOException {
        final BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(qrImage, QR_IMAGE_EXTENSION, outputStream);
            return outputStream.toByteArray();
        }
    }
}
