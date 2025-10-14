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

    private static final String QUERY_PARAMETER_DELIMITER = "\\?";

    @Column(name = "image_url")
    private String value;

    public ImageUrl(final String presignedUrl) {
        this.value = presignedUrl.split(QUERY_PARAMETER_DELIMITER)[0];
    }
}
