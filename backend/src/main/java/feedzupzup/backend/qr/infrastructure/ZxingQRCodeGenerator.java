package feedzupzup.backend.qr.infrastructure;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import feedzupzup.backend.qr.infrastructure.exception.QRGenerationException;
import feedzupzup.backend.qr.service.QRCodeGenerator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class ZxingQRCodeGenerator implements QRCodeGenerator {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 300;
    public static final String QR_IMAGE_EXTENSION = "PNG";

    public byte[] generateQRCode(final String url) {
        final QRCodeWriter qrCodeWriter = new QRCodeWriter();

        final Map<EncodeHintType, Object> options = new HashMap<>();
        options.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        options.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        options.put(EncodeHintType.MARGIN, 1);

        try {
            final BitMatrix bitMatrix = qrCodeWriter.encode(
                    url,
                    BarcodeFormat.QR_CODE,
                    WIDTH,
                    HEIGHT,
                    options
            );

            final BufferedImage qrImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

            qrImage.createGraphics();
            final Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, WIDTH, HEIGHT);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    if (bitMatrix.get(j, i)) {
                        graphics.fillRect(j, i, 1, 1);
                    }
                }
            }

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, QR_IMAGE_EXTENSION, outputStream);

            return outputStream.toByteArray();
        } catch (final WriterException | IOException e) {
            throw new QRGenerationException();
        }
    }
}
