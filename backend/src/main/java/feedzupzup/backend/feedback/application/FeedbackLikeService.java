package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.UserLikeFeedbacksRepository;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackLikeService {

    private final FeedbackRepository feedBackRepository;
    private final UserLikeFeedbacksRepository userLikeFeedbacksRepository;

    @Transactional
    public LikeResponse like(final Long feedbackId, final UUID visitorId) {
        userLikeFeedbacksRepository.saveUser(visitorId);

        final Feedback feedback = findFeedbackBy(feedbackId);
        if (userLikeFeedbacksRepository.isAlreadyLike(visitorId, feedbackId)) {
            throw new IllegalArgumentException(
                    "해당 유저" + visitorId + "는 이미 해당 feedbackId" + feedbackId + "에 좋아요를 눌렀습니다.");
        }

        feedback.increaseLikeCount();
        userLikeFeedbacksRepository.save(visitorId, feedbackId);

        return LikeResponse.from(feedback);
    }

    @Transactional
    public LikeResponse unLike(final Long feedbackId) {
        final Feedback feedback = findFeedbackBy(feedbackId);
        feedback.decreaseLikeCount();
        return LikeResponse.from(feedback);
    }

    private Feedback findFeedbackBy(final Long feedbackId) {
        return feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "feedbackId " + feedbackId + "는 존재하지 않습니다."));
    }
}
