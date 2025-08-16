package feedzupzup.backend.qr.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import feedzupzup.backend.organization.domain.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QR extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    private Organization organization;

    public QR(
            @NonNull final String imageUrl,
            @NonNull final Organization organization
    ) {
        this.imageUrl = imageUrl;
        this.organization = organization;
    }
}
