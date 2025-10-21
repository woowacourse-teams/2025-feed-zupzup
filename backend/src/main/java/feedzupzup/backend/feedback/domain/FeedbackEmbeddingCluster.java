package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import feedzupzup.backend.global.convert.DoubleArrayConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE feedback_embedding_cluster SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class FeedbackEmbeddingCluster extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "similarity_score")
    private double similarityScore;

    @Column(name = "embedding_vector", columnDefinition = "TEXT")
    @Convert(converter = DoubleArrayConverter.class)
    private double[] embeddingVector;

    @OneToOne(fetch = FetchType.LAZY)
    private Feedback feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    private EmbeddingCluster embeddingCluster;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    private FeedbackEmbeddingCluster(
            final double similarityScore,
            final double[] embeddingVector,
            final Feedback feedback,
            final EmbeddingCluster embeddingCluster
    ) {
        this.similarityScore = similarityScore;
        this.embeddingVector = embeddingVector;
        this.feedback = feedback;
        this.embeddingCluster = embeddingCluster;
    }

    public static FeedbackEmbeddingCluster createNewCluster(final double[] embedding, final Feedback feedback, final EmbeddingCluster embeddingCluster) {
        return new FeedbackEmbeddingCluster( 1.0, embedding, feedback, embeddingCluster);
    }

    public FeedbackEmbeddingCluster assignMyCluster(final Feedback feedback, final double[] embedding) {
        double similarity = calculateSimilarityTo(embedding);
        return new FeedbackEmbeddingCluster(similarity, embedding, feedback, this.embeddingCluster);
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

    public boolean isEmptyLabel() {
        return embeddingCluster.getLabel() == null ||  embeddingCluster.getLabel().isEmpty();
    }

    public String getFeedbackContentValue() {
        return this.feedback.getContent().getValue();
    }
}
