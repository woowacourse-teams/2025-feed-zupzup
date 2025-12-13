package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.ClusterLabelGenerator;
import feedzupzup.backend.feedback.domain.EmbeddingCluster;
import feedzupzup.backend.feedback.domain.EmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.exception.ClusterException.EmbeddingExtractionFailedException;
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

    private static final double SIMILARITY_THRESHOLD = 0.83;
    private static final List<Integer> NEW_CLUSTER_LABEL_THRESHOLDS = List.of(1, 5, 15, 30);

    private final EmbeddingService embeddingService;
    private final ClusterLabelGenerator clusterLabelGenerator;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackEmbeddingClusterRepository feedbackEmbeddingClusterRepository;
    private final EmbeddingClusterRepository embeddingClusterRepository;
    private final VoyageRetryQueueService retryQueueService;

    /**
     * 최초 클러스터링 (재시도 스케줄링 포함)
     */
    @Transactional
    public Long cluster(final Long createdFeedbackId) {
        final Feedback createdFeedback = getFeedback(createdFeedbackId);

        if (feedbackEmbeddingClusterRepository.existsByFeedback(createdFeedback)) {
            throw new AlreadyClusteringException("이미 클러스터링 된 피드백입니다. (feedbackId = " + createdFeedbackId + ")");
        }

        try {
            final double[] embedding = embeddingService.extractEmbedding(
                    createdFeedback.getContent().getValue()
            );
            return assignAndSaveCluster(createdFeedback, embedding);

        } catch (Exception e) {
            retryQueueService.retryTask(createdFeedbackId, e.getMessage());
            throw new EmbeddingExtractionFailedException("feedbackId = " + createdFeedbackId, e);
        }
    }

    /**
     * 재시도 클러스터링 (재시도 스케줄링 없음)
     * VoyageRetryTaskProcessor에서만 호출
     */
    @Transactional
    public Long clusterForRetry(final Long createdFeedbackId) {
        final Feedback createdFeedback = getFeedback(createdFeedbackId);

        // 이미 완료된 경우 조기 종료
        Optional<FeedbackEmbeddingCluster> existingCluster =
                feedbackEmbeddingClusterRepository.findByFeedback(createdFeedback);
        if (existingCluster.isPresent()) {
            log.info("[재시도] 이미 클러스터링 완료됨: feedbackId={}, clusterId={}",
                    createdFeedbackId, existingCluster.get().getId());
            return existingCluster.get().getId();
        }

        // 재시도 스케줄링 없이 바로 실패 (TaskProcessor가 재시도 관리)
        final double[] embedding = embeddingService.extractEmbedding(
                createdFeedback.getContent().getValue()
        );
        return assignAndSaveCluster(createdFeedback, embedding);
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

    /**
     * 클러스터 할당 및 저장 (공통 로직)
     */
    private Long assignAndSaveCluster(final Feedback createdFeedback, final double[] embedding) {
        final Optional<FeedbackEmbeddingCluster> assignedCluster = assignCluster(createdFeedback, embedding);

        if (assignedCluster.isEmpty()) {
            // 새 클러스터 생성
            final EmbeddingCluster empty = EmbeddingCluster.createEmpty();
            embeddingClusterRepository.save(empty);
            final FeedbackEmbeddingCluster newCluster = FeedbackEmbeddingCluster.createNewCluster(
                    embedding, createdFeedback, empty
            );
            return feedbackEmbeddingClusterRepository.save(newCluster).getId();
        }

        // 기존 클러스터에 할당
        return feedbackEmbeddingClusterRepository.save(assignedCluster.get()).getId();
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
}
