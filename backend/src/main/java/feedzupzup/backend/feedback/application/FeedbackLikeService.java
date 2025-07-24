package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeRepository;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackLikeService {

    private final FeedbackLikeRepository feedbackLikeRepository;
    private final FeedBackRepository feedBackRepository;

    public LikeResponse like(final Long feedbackId) {
        validateExistFeedback(feedbackId);
        final int beforeLikeCount = feedbackLikeRepository.getLikeCount(feedbackId);
        final int afterLikeCount = feedbackLikeRepository.increaseAndGet(feedbackId);
        return new LikeResponse(beforeLikeCount, afterLikeCount);
    }

    public LikeResponse unLike(final Long feedbackId) {
        validateExistFeedback(feedbackId);
        final int beforeLikeCount = feedbackLikeRepository.getLikeCount(feedbackId);
        final int afterLikeCount = feedbackLikeRepository.decreaseAndGet(feedbackId);
        return new LikeResponse(beforeLikeCount, afterLikeCount);
    }

    @Transactional
    public void flushLikeCountBuffer() {
        final Map<Long, Integer> likeCounts = feedbackLikeRepository.getLikeCounts();
        final List<Long> feedbackIds = likeCounts.keySet().stream().toList();
        final List<Feedback> feedbacks = feedBackRepository.findByIdIn(feedbackIds);
        for (Feedback feedback : feedbacks) {
            feedback.updateLikeCount(feedback.getLikeCount() + likeCounts.get(feedback.getId()));
        }
        feedbackLikeRepository.clear();
    }

    private void validateExistFeedback(Long feedbackId) {
        if (!feedBackRepository.existsById(feedbackId)) {
            throw new ResourceNotFoundException("피드백을 찾을 수 없습니다.");
        }
    }
}
