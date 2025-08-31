package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeRepository;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackLikeService {

    private final FeedbackLikeRepository feedbackLikeRepository;
    private final FeedbackRepository feedBackRepository;

    @Transactional
    public LikeResponse like(final Long feedbackId) {
        final Feedback feedback = findFeedbackBy(feedbackId);
        feedback.updateLikeCount(1);
        return new LikeResponse(feedback.getLikeCount());
    }

    @Transactional
    public LikeResponse unLike(final Long feedbackId) {
        final Feedback feedback = findFeedbackBy(feedbackId);
        feedback.updateLikeCount(-1);
        return new LikeResponse(feedback.getLikeCount());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void flushLikeCountBuffer() {
        final Map<Long, Integer> likeCounts = feedbackLikeRepository.getLikeCounts();
        final List<Long> feedbackIds = likeCounts.keySet().stream().toList();
        final List<Feedback> feedbacks = feedBackRepository.findByIdIn(feedbackIds);
        for (Feedback feedback : feedbacks) {
            feedback.updateLikeCount(feedback.getLikeCount() + likeCounts.get(feedback.getId()));
        }
        feedbackLikeRepository.clear();
    }

    private Feedback findFeedbackBy(final Long feedbackId) {
        return feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("feedbackId " + feedbackId + "는 존재하지 않습니다."));
    }

    private void validateExistFeedback(Long feedbackId) {
        if (!feedBackRepository.existsById(feedbackId)) {
            throw new ResourceNotFoundException("피드백을 찾을 수 없습니다.");
        }
    }
}
