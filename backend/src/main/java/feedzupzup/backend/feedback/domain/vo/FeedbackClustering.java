package feedzupzup.backend.feedback.domain.vo;

import feedzupzup.backend.global.convert.DoubleArrayConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record FeedbackClustering(
        @Column(name = "cluster_id", columnDefinition = "BINARY(16)")
        UUID clusterId,
        @Column(name = "similarity_score")
        double similarityScore,
        @Column(name = "embedding_vector", columnDefinition = "TEXT")
        @Convert(converter = DoubleArrayConverter.class)
        double[] embeddingVector
) {

    public static FeedbackClustering createNewCluster(final double[] embedding) {
        return new FeedbackClustering(UUID.randomUUID(), 1.0, embedding);
    }

    public FeedbackClustering assignMyCluster(final double[] embedding) {
        double similarity = calculateSimilarityTo(embedding);
        return new FeedbackClustering(this.clusterId, similarity, embedding);
    }

    private double calculateSimilarityTo(final double[] other) {
        if (this.embeddingVector.length != other.length) {
            throw new IllegalArgumentException("벡터 차원 불일치");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < embeddingVector.length; i++) {
            dotProduct += embeddingVector[i] * other[i];
            normA += embeddingVector[i] * embeddingVector[i];
            normB += other[i] * other[i];
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
