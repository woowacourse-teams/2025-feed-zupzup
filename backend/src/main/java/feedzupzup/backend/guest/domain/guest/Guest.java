package feedzupzup.backend.guest.domain.guest;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Guest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID visitorUuid;

    private LocalDateTime connectedTime;

    public Guest(@NonNull final UUID visitorUuid, @NonNull final LocalDateTime connectedTime) {
        this.visitorUuid = visitorUuid;
        this.connectedTime = connectedTime;
    }

    public boolean isPersisted() {
        return this.id != null;
    }
}
