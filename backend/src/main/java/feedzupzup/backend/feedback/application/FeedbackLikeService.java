package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.LIKES;

import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.LikeFeedbacks;
import feedzupzup.backend.feedback.domain.UserLikeFeedbacksRepository;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import feedzupzup.backend.feedback.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.event.FeedbackCacheEvent;
import feedzupzup.backend.feedback.exception.FeedbackException.DuplicateLikeException;
import feedzupzup.backend.feedback.exception.FeedbackException.InvalidLikeException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.domain.like.LikeHistory;
import feedzupzup.backend.guest.domain.like.LikeHistoryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackLikeService {

    private final FeedbackRepository feedBackRepository;
    private final UserLikeFeedbacksRepository userLikeFeedbacksRepository;
    private final GuestRepository guestRepository;
    private final LikeHistoryRepository likeHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LikeResponse like(final Long feedbackId, final Guest guest) {
        final Guest savedGuest = saveGuestIfNotPersisted(guest);
        final Feedback feedback = findFeedbackBy(feedbackId);

        if (likeHistoryRepository.existsByGuestAndFeedback(savedGuest, feedback)) {
            throw new DuplicateLikeException(
                    "해당 유저 " + savedGuest.getVisitorUuid() + "는 이미 해당 feedbackId " + feedbackId + "에 좋아요를 눌렀습니다.");
        }
        feedback.increaseLikeCount();

        // 캐시 핸들 이벤트 발행
        publishLikesFeedbackCacheEvent(FeedbackItem.from(feedback), feedback.getOrganization().getUuid());

        likeHistoryRepository.save(new LikeHistory(savedGuest, feedback));
        return LikeResponse.from(feedback);
    }

    @Transactional
    public LikeResponse unlike(final Long feedbackId, final Guest guest) {
        final Guest savedGuest = saveGuestIfNotPersisted(guest);
        final Feedback feedback = findFeedbackBy(feedbackId);

        if (!likeHistoryRepository.existsByGuestAndFeedback(savedGuest, feedback)) {
            throw new InvalidLikeException(
                    "해당 유저 " + savedGuest.getVisitorUuid() + "는 feedbackId " + feedbackId + "에 좋아요 기록이 없습니다.");
        }
        feedback.decreaseLikeCount();

        // 캐시 핸들 이벤트 발행
        publishLikesFeedbackCacheEvent(FeedbackItem.from(feedback), feedback.getOrganization().getUuid());

        likeHistoryRepository.deleteByGuestAndFeedback(savedGuest, feedback);
        return LikeResponse.from(feedback);
    }

    private Guest saveGuestIfNotPersisted(final Guest guest) {
        if (!guest.isPersisted()) {
            return guestRepository.save(guest);
        }
        return guest;
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

    private void publishLikesFeedbackCacheEvent(final FeedbackItem feedbackItem, final UUID organizationUuid) {
        final FeedbackCacheEvent event = new FeedbackCacheEvent(feedbackItem, organizationUuid, LIKES);
        eventPublisher.publishEvent(event);
    }
}
