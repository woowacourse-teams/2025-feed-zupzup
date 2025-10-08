package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
            SELECT f
            FROM Feedback f
            WHERE f.organization.uuid = :organizationUuid
            AND (:status IS NULL OR f.status = :status)
            AND (:cursorId IS NULL OR f.id < :cursorId)
            AND f.deletedAt IS NULL
            ORDER BY f.id DESC
            """)
    List<Feedback> findByLatest(
            @Param("organizationUuid") UUID organizationUuid,
            @Param("status") ProcessStatus status,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
            SELECT f
            FROM Feedback f
            WHERE f.organization.uuid = :organizationUuid
            AND (:status IS NULL OR f.status = :status)
            AND (:cursorId IS NULL OR f.id > :cursorId)
            AND f.deletedAt IS NULL
            ORDER BY f.id ASC
            """)
    List<Feedback> findByOldest(
            @Param("organizationUuid") UUID organizationUuid,
            @Param("status") ProcessStatus status,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
            SELECT f
            FROM Feedback f
            WHERE f.organization.uuid = :organizationUuid
            AND (:status IS NULL OR f.status = :status)
            AND (
                :cursorId IS NULL OR 
                f.likeCount < (SELECT f2.likeCount FROM Feedback f2 WHERE f2.id = :cursorId) OR 
                (f.likeCount = (SELECT f2.likeCount FROM Feedback f2 WHERE f2.id = :cursorId) AND f.id < :cursorId)
            )
            AND f.deletedAt IS NULL
            ORDER BY f.likeCount DESC, f.id ASC
            """)
    List<Feedback> findByLikes(
            @Param("organizationUuid") UUID organizationUuid,
            @Param("status") ProcessStatus status,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    List<Feedback> findByIdIn(Collection<Long> ids);

    List<Feedback> findByOrganizationUuidAndIdIn(final UUID uuid, final Collection<Long> id);

    @Query("""
            SELECT new feedzupzup.backend.feedback.domain.FeedbackAmount(
              COUNT(f),
              COALESCE(SUM(CASE WHEN f.status = feedzupzup.backend.feedback.domain.vo.ProcessStatus.CONFIRMED THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN f.status = feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING THEN 1L ELSE 0L END), 0L)
            )
            FROM Feedback f
            WHERE f.organization.id = :organizationId
            AND f.deletedAt IS NULL
            """)
    FeedbackAmount countFeedbackByOrganizationIdAndProcessStatus(final Long organizationId);

    @Query("""
            SELECT new feedzupzup.backend.feedback.domain.FeedbackAmount(
              COALESCE(SUM(1), 0L),
              COALESCE(SUM(CASE WHEN f.status = feedzupzup.backend.feedback.domain.vo.ProcessStatus.CONFIRMED THEN 1L ELSE 0L END), 0L),
              COALESCE(SUM(CASE WHEN f.status = feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING THEN 1L ELSE 0L END), 0L)
            )
            FROM Organizer org
            INNER JOIN Feedback f ON f.organization.id = org.organization.id
            WHERE org.admin.id = :adminId AND f.deletedAt IS NULL
            """)
    FeedbackAmount findFeedbackStatisticsByAdminId(Long adminId);

    void deleteAllByOrganizationIdIn(List<Long> organizationIds);

    void deleteAllByOrganizationId(Long organizationId);

    @Query(value = """
                SELECT 
                    BIN_TO_UUID(f.cluster_id) AS clusterId,
                    (
                        SELECT f2.content
                        FROM feedback f2
                        WHERE f2.cluster_id = f.cluster_id
                        ORDER BY f2.created_at ASC
                        LIMIT 1
                    ) AS content,
                    COUNT(f.id) AS totalCount
                FROM feedback f
                    JOIN organization org
                    ON f.organization_id = org.id
                WHERE org.uuid = :organizationUuid
                    AND f.deleted_at IS NULL
                    AND f.cluster_id IS NOT NULL
                GROUP BY f.cluster_id
                ORDER BY MIN(f.id)
            """, nativeQuery = true)
    List<ClusterRepresentativeFeedback> findAllRepresentativeFeedbackPerCluster(final UUID organizationUuid);

    List<Feedback> findAllByClustering_ClusterId(final UUID clusterId);

    @Query("""
                SELECT MIN(f.id)
                FROM Feedback f
                WHERE f.organization.uuid = :organizationUuid
                    AND f.deletedAt IS NULL
                    AND f.clustering.clusterId IS NOT NULL
                GROUP BY f.clustering.clusterId
            """)
    List<Long> findOldestFeedbackIdPerCluster(final UUID organizationUuid);

    @Query("""
                SELECT f
                FROM Feedback f
                WHERE f.id IN :feedbackIds
                ORDER BY f.id
            """)
    List<Feedback> findByIdIn(final List<Long> feedbackIds);
}
