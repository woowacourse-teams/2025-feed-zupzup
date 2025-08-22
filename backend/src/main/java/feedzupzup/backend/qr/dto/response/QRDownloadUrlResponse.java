package feedzupzup.backend.qr.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "QR 다운로드 URL 응답")
public record QRDownloadUrlResponse(
        @Schema(description = "QR 다운로드 URL", example = "https://example.com/download/qr-code/123e4567-e89b-12d3-a456-426614174000")
        String downloadUrl
) {

}
