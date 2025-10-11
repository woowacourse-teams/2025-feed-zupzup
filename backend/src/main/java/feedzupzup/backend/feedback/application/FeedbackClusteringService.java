package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.EmbeddingExtractor;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.vo.FeedbackClustering;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackClusteringService {

    private static final double SIMILARITY_THRESHOLD = 0.7;

    private final EmbeddingExtractor embeddingExtractor;
    private final FeedbackRepository feedbackRepository;

    // TODO : embedding 추출 작업 실패시 Fallback 전략
    // TODO : embedding 트랜잭션애 포함되어 커넥션 잡아먹음. 추후 수정 필요
    @Transactional
    public void cluster(final Long feedbackId) {
        Feedback feedback = getFeedback(feedbackId);
        double[] embedding = embeddingExtractor.extract(feedback.getContent().getValue());

        List<Feedback> representationFeedbackPerCluster = getRepresentationFeedbackPerCluster(
                feedback.getOrganization().getUuid());

        FeedbackClustering clustering = representationFeedbackPerCluster.stream()
                .map(representation -> representation.getClustering().assignMyCluster(embedding))
                .max(Comparator.comparingDouble(FeedbackClustering::similarityScore))
                .filter(best -> best.similarityScore() >= SIMILARITY_THRESHOLD)
                .orElseGet(() -> FeedbackClustering.createNewCluster(embedding));

        feedback.updateClustering(clustering);
    }

    private List<Feedback> getRepresentationFeedbackPerCluster(final UUID organizationUuid) {
        List<Long> oldestFeedbackIdsPerCluster = feedbackRepository.findOldestFeedbackIdPerCluster(organizationUuid);
        return feedbackRepository.findByIdIn(oldestFeedbackIdsPerCluster);
    }

    private Feedback getFeedback(final Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + feedbackId + ")인 피드백을 찾을 수 없습니다."));
    }
}
