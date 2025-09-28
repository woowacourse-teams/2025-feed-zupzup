package feedzupzup.backend.feedback.application;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.service.FeedbackSortStrategy;
import feedzupzup.backend.feedback.domain.service.FeedbackSortStrategyFactory;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortBy;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.event.FeedbackCreatedEvent;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.log.BusinessActionLog;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserFeedbackService {

    private final FeedbackRepository feedBackRepository;
    private final FeedbackSortStrategyFactory feedbackSortStrategyFactory;
    private final OrganizationRepository organizationRepository;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @BusinessActionLog
    public CreateFeedbackResponse create(
            final CreateFeedbackRequest request,
            final UUID organizationUuid
    ) {
        final Organization organization = findOrganizationBy(organizationUuid);
        final Category category = Category.findCategoryBy(request.category());
        final OrganizationCategory organizationCategory = organization.findOrganizationCategoryBy(
                category);
        final Feedback newFeedback = request.toFeedback(organization, organizationCategory);
        final Feedback savedFeedback = feedBackRepository.save(newFeedback);

        // 최신순 캐시 업데이트
        updateLatestCache(organizationUuid, savedFeedback);

        // 새로운 피드백이 생성되면 이벤트 발행
        publishFeedbackCreatedEvent(organization);

        return CreateFeedbackResponse.from(savedFeedback);
    }

    private void updateLatestCache(final UUID organizationUuid, final Feedback savedFeedback) {
        final Cache latestFeedbacks = cacheManager.getCache("latestFeedbacks");
        if (latestFeedbacks != null) {
            List<Feedback> cachedFeedbacks = latestFeedbacks.get(organizationUuid, List.class);
            if (cachedFeedbacks != null) {
                if (cachedFeedbacks.size() >= 10) {
                    final Feedback feedback = cachedFeedbacks.removeLast();
                    log.info("캐시 업데이트 : feedbackId " + feedback.getId() + "삭제");
                }
                cachedFeedbacks.addFirst(savedFeedback);
                log.info("캐시 업데이트 : feedbackId " + savedFeedback.getId() + "추가");
            }
        }
    }

    public UserFeedbackListResponse getFeedbackPage(
            final UUID organizationUuid,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackSortBy sortBy
    ) {

        final Pageable pageable = createPageable(size);
        FeedbackSortStrategy feedbackSortStrategy = feedbackSortStrategyFactory.find(sortBy);
        List<Feedback> feedbacks = feedbackSortStrategy.getSortedFeedbacks(organizationUuid, status, cursorId, pageable);
        final FeedbackPage feedbackPage = FeedbackPage.createCursorPage(feedbacks, size);

        return UserFeedbackListResponse.of(
                feedbackPage.getFeedbacks(),
                feedbackPage.isHasNext(),
                feedbackPage.calculateNextCursorId()
        );
    }

    public MyFeedbackListResponse getMyFeedbackPage(
            final UUID organizationUuid,
            final FeedbackSortBy sortBy,
            final List<Long> myFeedbackIds
    ) {

        final List<Feedback> feedbacks = feedBackRepository.findByOrganizationUuidAndIdIn(
                organizationUuid, myFeedbackIds);

        final FeedbackSortStrategy feedbackSortStrategy = feedbackSortStrategyFactory.find(sortBy);
        final List<Feedback> sortedFeedbacks = feedbackSortStrategy.sort(feedbacks);

        return MyFeedbackListResponse.of(sortedFeedbacks);
    }

    private Organization findOrganizationBy(final UUID organizationUuid) {
        return organizationRepository.findByUuid(organizationUuid)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
    }

    private Pageable createPageable(int size) {
        final Pageable pageable = Pageable.ofSize(size + 1);
        return pageable;
    }

    private void publishFeedbackCreatedEvent(Organization organization) {
        FeedbackCreatedEvent event = new FeedbackCreatedEvent(organization.getId(), "피드줍줍");
        eventPublisher.publishEvent(event);
    }
}
