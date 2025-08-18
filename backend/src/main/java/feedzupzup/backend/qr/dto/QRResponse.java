package feedzupzup.backend.qr.dto;

import feedzupzup.backend.qr.domain.QR;

public record QRResponse(
        String imageUrl,
        String siteUrl
) {

    public static QRResponse of(final QR qr, final String siteUrl) {
        return new QRResponse(qr.getImageUrl(), siteUrl);
    }
}
