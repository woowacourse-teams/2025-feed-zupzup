package feedzupzup.backend.guest.domain.like;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.guest.domain.guest.Guest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeHistoryRepository extends JpaRepository<LikeHistory, Long> {

    boolean existsByGuestAndFeedback(final Guest guest, final Feedback feedback);

    int deleteByGuestAndFeedback(final Guest guest, final Feedback feedback);

    @Query("""
            SELECT DISTINCT lh
            FROM LikeHistory lh
            JOIN FETCH lh.guest g
            JOIN FETCH lh.feedback fb
            JOIN FETCH fb.organization o
            WHERE g.guestUuid = :guestUuid
            AND o.uuid = :organizationUuid
            """)
    List<LikeHistory> findLikeHistoriesBy(
            @Param("guestUuid") final UUID guestUuid,
            @Param("organizationUuid") final UUID organizationUuid
    );

    @Modifying
    @Query("DELETE FROM LikeHistory lh WHERE lh.guest.id IN :guestIds")
    void deleteByGuestIdIn(@Param("guestIds") List<Long> guestIds);
}
