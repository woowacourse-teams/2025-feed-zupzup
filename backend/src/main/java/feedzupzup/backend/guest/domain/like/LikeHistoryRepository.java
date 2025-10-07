package feedzupzup.backend.guest.domain.like;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.guest.domain.guest.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeHistoryRepository extends JpaRepository<LikeHistory, Long> {

    boolean existsByGuestAndFeedback(Guest guest, Feedback feedback);
}
