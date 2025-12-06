package feedzupzup.backend.s3.service;

import feedzupzup.backend.global.exception.BusinessViolationException.NotSupportedException;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum S3ObjectType {

    JPEG("image/jpeg", "IMAGE", "jpg", "jpeg", "jpe", "jif", "jfif", "jfi"),
    PNG("image/png", "IMAGE", "png"),
    GIF("image/gif", "IMAGE", "gif"),
    WEBP("image/webp", "IMAGE", "webp"),
    TIFF("image/tiff", "IMAGE", "tiff", "tif"),
    BMP("image/bmp", "IMAGE", "bmp"),
    SVG("image/svg+xml", "IMAGE", "svg", "svgz"),
    ICO("image/x-icon", "IMAGE", "ico"),
    HEIC("image/heic", "IMAGE", "heic"),
    HEIF("image/heif", "IMAGE", "heif"),
    RAW("image/x-raw", "IMAGE", "raw", "arw", "cr2", "nrw", "k25"),
    PSD("image/vnd.adobe.photoshop", "IMAGE", "psd"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "DOCUMENT", "xlsx");

    private final String contentType;
    private final String mediaType;
    private final List<String> extensions;

    S3ObjectType(
            final String contentType,
            final String mediaType,
            final String... extensions
    ) {
        this.contentType = contentType;
        this.mediaType = mediaType;
        this.extensions = Arrays.asList(extensions);
    }

    public static S3ObjectType fromExtension(final String extension) {
        return Arrays.stream(values())
                .filter(type -> type.extensions.contains(extension.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new NotSupportedException("지원하지 않는 확장자입니다. " + extension));
    }
}
