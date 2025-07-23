package feedzupzup.backend.feedback.domain;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedBackRepository extends JpaRepository<Feedback, Long> {
    @Query("""
            SELECT f
            FROM Feedback f
            WHERE f.placeId = :placeId
            AND (:cursorId IS NULL OR f.id < :cursorId)
            ORDER BY f.id DESC
            """)
    List<Feedback> findPageByPlaceIdAndCursorIdOrderByDesc(
            final Long placeId,
            final Long cursorId,
            final Pageable pageable
    );
}
