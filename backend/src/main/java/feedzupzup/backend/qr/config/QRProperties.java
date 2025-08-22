package feedzupzup.backend.qr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "qr")
public record QRProperties(
        String baseUrl,
        Image image,
        Generation generation
) {

    public record Image(
            int width,
            int height,
            String extension
    ) {

    }

    public record Generation(
            String errorCorrectionLevel,
            String characterSet,
            int margin
    ) {

    }
}
