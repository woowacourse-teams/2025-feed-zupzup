package feedzupzup.backend.feedback.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
             SELECT f
             FROM Feedback f
             WHERE f.organizationId = :organizationId
             AND (:status IS NULL OR f.status = :status)
             AND (
                 CASE
                     WHEN :orderBy = 'LATEST' THEN (:cursorId IS NULL OR f.id < :cursorId)
                     WHEN :orderBy = 'OLDEST' THEN (:cursorId IS NULL OR f.id > :cursorId)
                     WHEN :orderBy = 'LIKES' THEN (:cursorId IS NULL OR f.likeCount <
                         (SELECT f2.likeCount FROM Feedback f2 WHERE f2.id = :cursorId))
                     ELSE (:cursorId IS NULL OR f.id < :cursorId)
                 END
             )
             ORDER BY
             CASE WHEN :orderBy IS NULL THEN f.id END DESC,
             CASE WHEN :orderBy = 'LATEST' THEN f.id END DESC,
             CASE WHEN :orderBy = 'OLDEST' THEN f.id END ASC,
             CASE WHEN :orderBy = 'LIKES' THEN f.likeCount END DESC
            """)
    List<Feedback> findByOrganizationIdAndProcessStatusAndCursor(
            final Long organizationId,
            final Long cursorId,
            final Pageable pageable,
            final ProcessStatus status,
            final String orderBy
    );

    List<Feedback> findByIdIn(Collection<Long> ids);

    @Query("""
             SELECT f
             FROM Feedback f
             WHERE f.organizationId = :organizationId
             AND f.id IN :ids
             AND (:status IS NULL OR f.status = :status)
             AND (
                 CASE
                     WHEN :orderBy = 'LATEST' THEN (:cursorId IS NULL OR f.id < :cursorId)
                     WHEN :orderBy = 'OLDEST' THEN (:cursorId IS NULL OR f.id > :cursorId)
                     WHEN :orderBy = 'LIKES' THEN (:cursorId IS NULL OR f.likeCount <
                         (SELECT f2.likeCount FROM Feedback f2 WHERE f2.id = :cursorId))
                     ELSE (:cursorId IS NULL OR f.id < :cursorId)
                 END
             )
             ORDER BY
             CASE WHEN :orderBy IS NULL THEN f.id END DESC,
             CASE WHEN :orderBy = 'LATEST' THEN f.id END DESC,
             CASE WHEN :orderBy = 'OLDEST' THEN f.id END ASC,
             CASE WHEN :orderBy = 'LIKES' THEN f.likeCount END DESC
            """)
    List<Feedback> findByOrganizationIdAndIdsAndProcessStatusAndCursor(
            final Long organizationId,
            final List<Long> ids,
            final Long cursorId,
            final Pageable pageable,
            final ProcessStatus status,
            final String orderBy
    );

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
