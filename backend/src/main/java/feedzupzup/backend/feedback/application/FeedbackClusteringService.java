package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.ClusterLabelGenerator;
import feedzupzup.backend.feedback.domain.EmbeddingCluster;
import feedzupzup.backend.feedback.domain.EmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.EmbeddingExtractor;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.exception.FeedbackException.AlreadyClusteringException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackClusteringService {

    private static final double SIMILARITY_THRESHOLD = 0.75;
    private static final List<Integer> NEW_CLUSTER_LABEL_THRESHOLDS = List.of(1, 5, 15, 30);

    private final EmbeddingExtractor embeddingExtractor;
    private final ClusterLabelGenerator clusterLabelGenerator;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackEmbeddingClusterRepository feedbackEmbeddingClusterRepository;
    private final EmbeddingClusterRepository embeddingClusterRepository;

    // TODO : embedding 트랜잭션애 포함되어 커넥션 잡아먹음. 추후 수정 필요
    @Transactional
    public FeedbackEmbeddingCluster cluster(final Long createdFeedbackId) {
        final Feedback createdFeedback = getFeedback(createdFeedbackId);
        if (feedbackEmbeddingClusterRepository.existsByFeedback(createdFeedback)) {
            throw new AlreadyClusteringException("이미 클러스터링 된 피드백입니다. (feedabckId = " + createdFeedbackId + ")");
        }
        final double[] createdFeedbackEmbedding = embeddingExtractor.extract(createdFeedback.getContent().getValue());

        final Optional<FeedbackEmbeddingCluster> assignedCluster = assignCluster(createdFeedback, createdFeedbackEmbedding);

        if (assignedCluster.isEmpty()) {
            final EmbeddingCluster empty = EmbeddingCluster.createEmpty();
            embeddingClusterRepository.save(empty);
            final FeedbackEmbeddingCluster newCluster = FeedbackEmbeddingCluster.createNewCluster(createdFeedbackEmbedding,
                    createdFeedback, empty);
            return feedbackEmbeddingClusterRepository.save(newCluster);
        }

        return feedbackEmbeddingClusterRepository.save(assignedCluster.get());
    }

    private Optional<FeedbackEmbeddingCluster> assignCluster(final Feedback createdFeedback, final double[] createdFeedbackEmbedding) {
        double originClusterScore = 1.0;
        final List<FeedbackEmbeddingCluster> representations = feedbackEmbeddingClusterRepository.findAllRepresentativeClusters(
                createdFeedback.getOrganization().getUuid(), originClusterScore);

        return representations.stream()
                .map(representation -> representation.assignMyCluster(createdFeedback, createdFeedbackEmbedding))
                .max(Comparator.comparingDouble(FeedbackEmbeddingCluster::getSimilarityScore))
                .filter(bestSimilarity -> bestSimilarity.getSimilarityScore() >= SIMILARITY_THRESHOLD);
    }

    private Feedback getFeedback(final Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + feedbackId + ")인 피드백을 찾을 수 없습니다."));
    }

    @Transactional
    public void createLabel(final Long createdClusterId) {
        final FeedbackEmbeddingCluster feedbackEmbeddingCluster = feedbackEmbeddingClusterRepository.findById(
                createdClusterId).orElseThrow(() -> new ResourceNotFoundException("해당 clusterId(id=" + createdClusterId + ")로 찾을 수 없습니다."));

        final List<FeedbackEmbeddingCluster> feedbackEmbeddingClusters = feedbackEmbeddingClusterRepository
                .findAllByEmbeddingCluster(feedbackEmbeddingCluster.getEmbeddingCluster());
        if (!NEW_CLUSTER_LABEL_THRESHOLDS.contains(feedbackEmbeddingClusters.size()) && !feedbackEmbeddingCluster.isEmptyLabel()) {
            return;
        }

        final List<String> feedbackContents = feedbackEmbeddingClusters.stream()
                .map(FeedbackEmbeddingCluster::getFeedbackContentValue)
                .toList();
        final String label = clusterLabelGenerator.generate(feedbackContents);
        log.info("현재 생성된 라벨: {}", label);

        final EmbeddingCluster embeddingCluster = feedbackEmbeddingCluster.getEmbeddingCluster();
        embeddingCluster.updateLabel(label);
    }
}
