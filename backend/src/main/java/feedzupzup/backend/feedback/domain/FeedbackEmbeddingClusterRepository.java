package feedzupzup.backend.feedback.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackEmbeddingClusterRepository extends JpaRepository<FeedbackEmbeddingCluster, Long> {

    @Query("""
    SELECT fec
    FROM FeedbackEmbeddingCluster fec
    JOIN fec.feedback f
    JOIN f.organization org
    WHERE org.uuid = :organizationUuid
      AND fec.similarityScore = :representationScore
    """)
    List<FeedbackEmbeddingCluster> findAllRepresentativeClusters(
            @Param("organizationUuid") UUID organizationUuid,
            @Param("representationScore") Double representationScore
    );

    List<FeedbackEmbeddingCluster> findAllByEmbeddingCluster(EmbeddingCluster embeddingCluster);

    boolean existsByFeedback(Feedback feedback);

    void deleteByFeedback_Id(Long feedbackId);
}
