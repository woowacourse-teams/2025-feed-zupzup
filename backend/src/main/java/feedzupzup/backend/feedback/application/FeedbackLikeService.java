package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeInMemoryRepository;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackLikeService {

    private final FeedbackLikeInMemoryRepository feedbackLikeInMemoryRepository;
    private final FeedBackRepository feedBackRepository;

    public LikeResponse like(final Long feedbackId) {
        final int beforeLikeCount = feedbackLikeInMemoryRepository.getLikeCount(feedbackId);
        final int afterLikeCount = feedbackLikeInMemoryRepository.increaseAndGet(feedbackId);
        return new LikeResponse(beforeLikeCount, afterLikeCount);
    }

    public LikeResponse unLike(final Long feedbackId) {
        final int beforeLikeCount = feedbackLikeInMemoryRepository.getLikeCount(feedbackId);
        final int afterLikeCount = feedbackLikeInMemoryRepository.decreaseAndGet(feedbackId);
        return new LikeResponse(beforeLikeCount, afterLikeCount);
    }

    @Transactional
    public void flushLikeCountBuffer() {
        final Map<Long, Integer> likeCounts = feedbackLikeInMemoryRepository.getLikeCounts();
        final List<Long> feedbackIds = likeCounts.keySet().stream().toList();
        final List<Feedback> feedbacks = feedBackRepository.findByIdIn(feedbackIds);
        for (Feedback feedback : feedbacks) {
            feedback.updateLikeCount(feedback.getLikeCount() + likeCounts.get(feedback.getId()));
        }
        feedbackLikeInMemoryRepository.clear();
    }
}
