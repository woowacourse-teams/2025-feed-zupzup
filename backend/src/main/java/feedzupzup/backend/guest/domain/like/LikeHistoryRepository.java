package feedzupzup.backend.guest.domain.like;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.guest.domain.guest.Guest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LikeHistoryRepository extends JpaRepository<LikeHistory, Long> {

    boolean existsByGuestAndFeedback(Guest guest, Feedback feedback);

    void deleteByGuestAndFeedback(Guest guest, Feedback feedback);

    @Query("""
            SELECT DISTINCT lh
            FROM LikeHistory lh
            JOIN FETCH lh.guest g
            JOIN FETCH lh.feedback fb
            JOIN FETCH fb.organization o
            WHERE g.id = :guestId
            AND o.uuid = :organizationUuid
            """)
    List<LikeHistory> findLikeHistoriesBy(Long guestId, UUID organizationUuid);
}
