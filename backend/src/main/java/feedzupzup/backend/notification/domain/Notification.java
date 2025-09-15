package feedzupzup.backend.notification.domain;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO. 추후 기기당 토큰의 중복을 허용할지 여부를 결정해야 함
    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    private Admin admin;

    public Notification(@NonNull Admin admin, @NonNull final String value) {
        this.admin = admin;
        this.value = value;
    }

    public void updateNotification(@NonNull final String value) {
        this.value = value;
    }
}
