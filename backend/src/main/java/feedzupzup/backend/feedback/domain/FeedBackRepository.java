package feedzupzup.backend.feedback.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedBackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
            SELECT f
            FROM Feedback f
            WHERE f.organizationId = :organizationId
            AND (:cursorId IS NULL OR f.id < :cursorId)
            ORDER BY f.id DESC
            """)
    List<Feedback> findPageByOrganizationIdAndCursorIdOrderByDesc(
            final Long organizationId,
            final Long cursorId,
            final Pageable pageable
    );

    List<Feedback> findByIdIn(Collection<Long> ids);

    @Query("""
            SELECT f
            FROM Feedback f
            WHERE f.placeId = :placeId
            AND f.postedAt.postedAt >= :dateTime
            """)
    List<Feedback> findByIdAndPostedAtAfter(Long placeId, LocalDateTime dateTime);
}
