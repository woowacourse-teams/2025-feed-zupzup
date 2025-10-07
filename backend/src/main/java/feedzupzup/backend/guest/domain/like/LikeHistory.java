package feedzupzup.backend.guest.domain.like;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.guest.domain.guest.Guest;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LikeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY)
    private Feedback feedback;

    public LikeHistory(@NonNull final Guest guest, @NonNull final Feedback feedback) {
        this.guest = guest;
        this.feedback = feedback;
    }
}
