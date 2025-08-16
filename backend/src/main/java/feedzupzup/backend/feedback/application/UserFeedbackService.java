package feedzupzup.backend.feedback.application;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeCounter;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.FeedbackOrderBy;
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
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFeedbackService {

    private final FeedbackRepository feedBackRepository;
    private final FeedbackLikeCounter feedbackLikeCounter;
    private final OrganizationRepository organizationRepository;
    private final FeedbackLikeService feedbackLikeService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @BusinessActionLog
    public CreateFeedbackResponse create(
            final CreateFeedbackRequest request,
            final Long organizationId
    ) {
        final Organization organization = findOrganizationBy(organizationId);
        final Category category = Category.findCategoryBy(request.category());
        final OrganizationCategory organizationCategory = organization.findOrganizationCategoryBy(
                category);
        final Feedback newFeedback = request.toFeedback(organization, organizationCategory);
        final Feedback savedFeedback = feedBackRepository.save(newFeedback);
        
        // 새로운 피드백이 생성되면 이벤트 발행
        publishFeedbackCreatedEvent(organization);

        return CreateFeedbackResponse.from(savedFeedback);
    }

    public UserFeedbackListResponse getFeedbackPage(
            final Long organizationId,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackOrderBy orderBy
    ) {
        final Pageable pageable = createPageable(size);

        feedbackLikeService.flushLikeCountBuffer();

        final List<Feedback> feedbacks = switch (orderBy) {
            case LATEST -> feedBackRepository.findByLatest(organizationId, status, cursorId, pageable);
            case OLDEST -> feedBackRepository.findByOldest(organizationId, status, cursorId, pageable);
            case LIKES -> feedBackRepository.findByLikes(organizationId, status, cursorId, pageable);
        };
        final FeedbackPage feedbackPage = FeedbackPage.createCursorPage(feedbacks, size);
        feedbackLikeCounter.applyBufferedLikeCount(feedbackPage.getFeedbacks());
        return UserFeedbackListResponse.of(
                feedbackPage.getFeedbacks(),
                feedbackPage.isHasNext(),
                feedbackPage.calculateNextCursorId()
        );
    }

    public MyFeedbackListResponse getMyFeedbackPage(
            final Long organizationId,
            final FeedbackOrderBy orderBy,
            final List<Long> myFeedbackIds
    ) {
        feedbackLikeService.flushLikeCountBuffer();

        final List<Feedback> feedbacks = feedBackRepository.findByOrganizationIdAndIdIn(
                organizationId, myFeedbackIds);

        final List<Feedback> sortedFeedbacks = sortFeedbacksByOrderBy(feedbacks, orderBy);
        
        feedbackLikeCounter.applyBufferedLikeCount(sortedFeedbacks);
        return MyFeedbackListResponse.of(sortedFeedbacks);
    }

    private Organization findOrganizationBy(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
    }

    private Pageable createPageable(int size) {
        final Pageable pageable = Pageable.ofSize(size + 1);
        return pageable;
    }

    private List<Feedback> sortFeedbacksByOrderBy(final List<Feedback> feedbacks, final FeedbackOrderBy orderBy) {
        if (orderBy == FeedbackOrderBy.LATEST) {
            return feedbacks.stream()
                    .sorted(Comparator.comparing(Feedback::getId).reversed())
                    .toList();
        }
        
        if (orderBy == FeedbackOrderBy.OLDEST) {
            return feedbacks.stream()
                    .sorted(Comparator.comparing(Feedback::getId))
                    .toList();
        }
        
        return feedbacks.stream()
                .sorted(Comparator.comparing(Feedback::getLikeCount).reversed()
                        .thenComparing(Feedback::getId))
                .toList();
    }
    
    private void publishFeedbackCreatedEvent(Organization organization) {
        FeedbackCreatedEvent event = new FeedbackCreatedEvent(organization.getId(), "피드줍줍");
        eventPublisher.publishEvent(event);
    }
}
