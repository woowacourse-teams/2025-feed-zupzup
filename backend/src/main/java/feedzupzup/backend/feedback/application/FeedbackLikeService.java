package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.application.FeedbackCacheManager.LikeAction;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.LikeFeedbacks;
import feedzupzup.backend.feedback.domain.UserLikeFeedbacksRepository;
import feedzupzup.backend.feedback.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.exception.FeedbackException.DuplicateLikeException;
import feedzupzup.backend.feedback.exception.FeedbackException.InvalidLikeException;
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
    private final FeedbackCacheManager feedbackCacheManager;

    @Transactional
    public LikeResponse like(final Long feedbackId, final UUID visitorId) {
        if (visitorId == null) {
            throw new InvalidLikeException("잘못된 요청입니다.");
        }

        final Feedback feedback = findFeedbackBy(feedbackId);
        if (userLikeFeedbacksRepository.isAlreadyLike(visitorId, feedbackId)) {
            throw new DuplicateLikeException(
                    "해당 유저 " + visitorId + "는 이미 해당 feedbackId" + feedbackId + "에 좋아요를 눌렀습니다.");
        }

        feedback.increaseLikeCount();
        feedbackCacheManager.handleLikesCache(feedback, LikeAction.INCREASE);
        userLikeFeedbacksRepository.save(visitorId, feedbackId);

        return LikeResponse.from(feedback);
    }

    @Transactional
    public LikeResponse unlike(final Long feedbackId, final UUID visitorId) {
        if (visitorId == null || !userLikeFeedbacksRepository.isAlreadyLike(visitorId,
                feedbackId)) {
            throw new InvalidLikeException(
                    "해당 유저 " + visitorId + "는 해당 feedbackId" + feedbackId
                            + "에 대한 좋아요 기록이 존재하지 않습니다."
            );
        }

        final Feedback feedback = findFeedbackBy(feedbackId);
        feedback.decreaseLikeCount();
        feedbackCacheManager.handleLikesCache(feedback, LikeAction.DECREASE);
        userLikeFeedbacksRepository.deleteLikeHistory(visitorId, feedbackId);

        return LikeResponse.from(feedback);
    }

    private Feedback findFeedbackBy(final Long feedbackId) {
        return feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "feedbackId " + feedbackId + "는 존재하지 않습니다."));
    }

    public LikeHistoryResponse findLikeHistories(final UUID visitorId) {
        final LikeFeedbacks likeFeedbacks = userLikeFeedbacksRepository.getUserLikeFeedbacksFrom(
                visitorId);
        return LikeHistoryResponse.from(likeFeedbacks);
    }
}
