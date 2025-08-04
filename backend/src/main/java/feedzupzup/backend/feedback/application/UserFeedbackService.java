package feedzupzup.backend.feedback.application;

import feedzupzup.backend.category.domain.AvailableCategories;
import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeCounter;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
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

    private final FeedBackRepository feedBackRepository;
    private final FeedbackLikeCounter feedbackLikeCounter;
    private final OrganizationRepository organizationRepository;

    @Transactional
    @BusinessActionLog
    public CreateFeedbackResponse create(
            final CreateFeedbackRequest request,
            final Long organizationId
    ) {
        final Organization organization = findOrganizationBy(organizationId);
        final Category category = getCategory(request, organization);
        final Feedback newFeedback = request.toFeedback(organization.getId(), category);
        final Feedback savedFeedback = feedBackRepository.save(newFeedback);
        return CreateFeedbackResponse.from(savedFeedback);
    }

    private Category getCategory(
            final CreateFeedbackRequest request,
            final Organization organization
    ) {
        final AvailableCategories availableCategories = new AvailableCategories(
                organization.getAvailableCategories()
        );
        if (!availableCategories.contains(request.category())) {
            throw new ResourceNotFoundException("존재하지 않는 카테고리입니다.");
        }
        return availableCategories.findCategoryBy(request.category());
    }

    public UserFeedbackListResponse getFeedbackPage(
            final Long organizationId,
            final int size,
            final Long cursorId
    ) {
        final Pageable pageable = Pageable.ofSize(size + 1);
        final List<Feedback> feedbacks = feedBackRepository.findPageByOrganizationIdAndCursorIdOrderByDesc(
                organizationId,
                cursorId,
                pageable
        );
        final FeedbackPage feedbackPage = FeedbackPage.createCursorPage(feedbacks, size);
        feedbackLikeCounter.applyBufferedLikeCount(feedbackPage);
        return UserFeedbackListResponse.of(
                feedbackPage.getFeedbacks(),
                feedbackPage.isHasNext(),
                feedbackPage.calculateNextCursorId()
        );
    }

    private Organization findOrganizationBy(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
    }
}
