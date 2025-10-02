package feedzupzup.backend.feedback.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUrl {

    @Column(name = "image_url")
    private String value;

    public ImageUrl(final String presignedUrl) {
        this.value = presignedUrl.split("\\?")[0];
    }
}
