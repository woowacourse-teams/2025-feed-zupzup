package feedzupzup.backend.admin.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record EncodedPassword(
        @Column(name = "password", nullable = false)
        String value
) {

}
