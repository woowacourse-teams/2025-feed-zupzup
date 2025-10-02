package feedzupzup.backend.feedback.domain;

import jakarta.persistence.Column;
import java.util.UUID;

public record FeedbackClustering(
        @Column(name = "cluster_id")
        UUID clusterId,
        @Column(name = "similarity_score")
        double similarityScore,
        @Column(name = "embedding_vector", columnDefinition = "TEXT")
        String embedding_vector
) {

}
