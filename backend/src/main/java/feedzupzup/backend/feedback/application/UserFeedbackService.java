package feedzupzup.backend.feedback.application;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeCounter;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.service.FeedbackSortStrategy;
import feedzupzup.backend.feedback.domain.service.FeedbackSortStrategyFactory;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortBy;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.log.BusinessActionLog;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFeedbackService {

    private final FeedbackRepository feedBackRepository;
    private final FeedbackLikeCounter feedbackLikeCounter;
    private final FeedbackSortStrategyFactory feedbackSortStrategyFactory;
    private final OrganizationRepository organizationRepository;
    private final FeedbackLikeService feedbackLikeService;

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
        return CreateFeedbackResponse.from(savedFeedback);
    }

    public UserFeedbackListResponse getFeedbackPage(
            final Long organizationId,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackSortBy sortBy
    ) {
        feedbackLikeService.flushLikeCountBuffer();

        final Pageable pageable = createPageable(size);
        FeedbackSortStrategy feedbackSortStrategy = feedbackSortStrategyFactory.find(sortBy);
        List<Feedback> feedbacks = feedbackSortStrategy.getSortedFeedbacks(organizationId, status, cursorId, pageable);
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
            final FeedbackSortBy sortBy,
            final List<Long> myFeedbackIds
    ) {
        feedbackLikeService.flushLikeCountBuffer();

        final List<Feedback> feedbacks = feedBackRepository.findByOrganizationIdAndIdIn(
                organizationId, myFeedbackIds);

        FeedbackSortStrategy feedbackSortStrategy = feedbackSortStrategyFactory.find(sortBy);
        final List<Feedback> sortedFeedbacks = feedbackSortStrategy.sort(feedbacks);
        
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
}
