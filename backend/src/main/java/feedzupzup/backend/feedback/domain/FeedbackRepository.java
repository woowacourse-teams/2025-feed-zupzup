package feedzupzup.backend.feedback.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
    SELECT f
    FROM Feedback f
    WHERE f.organizationId = :organizationId
    AND (:status IS NULL OR f.status = :status)
    AND (:cursorId IS NULL OR f.id < :cursorId)
    ORDER BY f.id DESC
    """)
    List<Feedback> findByLatest(
        @Param("organizationId") Long organizationId,
        @Param("status") ProcessStatus status,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    @Query("""
    SELECT f
    FROM Feedback f
    WHERE f.organizationId = :organizationId
    AND (:status IS NULL OR f.status = :status)
    AND (:cursorId IS NULL OR f.id > :cursorId)
    ORDER BY f.id ASC
    """)
    List<Feedback> findByOldest(
        @Param("organizationId") Long organizationId,
        @Param("status") ProcessStatus status,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    @Query("""
    SELECT f
    FROM Feedback f
    WHERE f.organizationId = :organizationId
    AND (:status IS NULL OR f.status = :status)
    AND (
        :cursorId IS NULL OR 
        f.likeCount < (SELECT f2.likeCount FROM Feedback f2 WHERE f2.id = :cursorId) OR 
        (f.likeCount = (SELECT f2.likeCount FROM Feedback f2 WHERE f2.id = :cursorId) AND f.id < :cursorId)
    )
    ORDER BY f.likeCount DESC, f.id ASC
    """)
    List<Feedback> findByLikes(
        @Param("organizationId") Long organizationId,
        @Param("status") ProcessStatus status,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    List<Feedback> findByIdIn(Collection<Long> ids);

    @Query("""
            SELECT f
            FROM Feedback f
            WHERE f.organizationId = :organizationId
            AND f.postedAt.postedAt >= :dateTime
            """)
    List<Feedback> findByOrganizationIdAndPostedAtAfter(
            Long organizationId,
            LocalDateTime dateTime
    );

}
