package feedzupzup.backend.feedback.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record ImageUrl(
        String imageUrl
) {

    public static ImageUrl createS3Url(final String presignedUrl) {
        String imageUrl = presignedUrl.split("\\?")[0];
        return new ImageUrl(imageUrl);
    }
}
