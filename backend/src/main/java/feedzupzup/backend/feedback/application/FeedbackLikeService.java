package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackLikeService {

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

    private Feedback findFeedbackBy(final Long feedbackId) {
        return feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("feedbackId " + feedbackId + "는 존재하지 않습니다."));
    }
}
