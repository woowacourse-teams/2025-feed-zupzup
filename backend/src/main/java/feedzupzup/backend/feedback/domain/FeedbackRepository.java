package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.organization.domain.Organization;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
            SELECT f
            FROM Feedback f
            JOIN FETCH f.organizationCategory
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
            JOIN FETCH f.organizationCategory
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
            JOIN FETCH f.organizationCategory
            WHERE f.organization.uuid = :organizationUuid
            AND (:status IS NULL OR f.status = :status)
            AND (
                :cursorId IS NULL OR 
                f.likeCount < (SELECT f2.likeCount FROM Feedback f2 WHERE f2.id = :cursorId) OR 
                (f.likeCount = (SELECT f2.likeCount FROM Feedback f2 WHERE f2.id = :cursorId) AND f.id < :cursorId)
            )
            AND f.deletedAt IS NULL
            ORDER BY f.likeCount.value DESC, f.id ASC
            """)
    List<Feedback> findByLikes(
            @Param("organizationUuid") UUID organizationUuid,
            @Param("status") ProcessStatus status,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    void deleteAllByOrganizationIdIn(List<Long> organizationIds);

    void deleteAllByOrganizationId(Long organizationId);

    @Query("""
        SELECT new feedzupzup.backend.feedback.domain.ClusterInfo(
            ec.id,
            ec.label,
            COUNT(fec.id)
        )
        FROM Feedback f
        JOIN f.organization org
        JOIN FeedbackEmbeddingCluster fec ON fec.feedback.id = f.id
        JOIN fec.embeddingCluster ec
        WHERE org.uuid = :organizationUuid
        GROUP BY ec.id, ec.label
        HAVING COUNT(fec.id) >= 1
        ORDER BY COUNT(fec.id) DESC
        """)
    List<ClusterInfo> findTopClusters(
            @Param("organizationUuid") UUID organizationUuid,
            Pageable pageable
            );

    List<Feedback> findByOrganization(Organization org);

    Page<Feedback> findByOrganization_Id(Long organizationId, Pageable pageable);}
