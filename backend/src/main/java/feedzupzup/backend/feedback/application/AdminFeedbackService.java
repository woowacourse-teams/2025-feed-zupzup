package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeCounter;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackSecretResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackStatusResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.log.BusinessActionLog;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminFeedbackService {

    private final FeedbackRepository feedBackRepository;
    private final FeedbackLikeCounter feedbackLikeCounter;
    private final FeedbackLikeService feedbackLikeService;

    @Transactional
    @BusinessActionLog
    public void delete(final Long feedbackId) {
        feedBackRepository.deleteById(feedbackId);
    }

    @Transactional
    @BusinessActionLog
    public UpdateFeedbackStatusResponse updateFeedbackStatus(
            final UpdateFeedbackStatusRequest request,
            final Long feedbackId
    ) {
        final Feedback feedback = getFeedback(feedbackId);
        feedback.updateStatus(request.status());
        return UpdateFeedbackStatusResponse.from(feedback);
    }

    @Transactional
    @BusinessActionLog
    public UpdateFeedbackSecretResponse updateFeedbackSecret(
            final Long feedbackId,
            final UpdateFeedbackSecretRequest request
    ) {
        final Feedback feedBack = getFeedback(feedbackId);
        feedBack.updateSecret(request.isSecret());
        return UpdateFeedbackSecretResponse.from(feedBack);
    }

    public AdminFeedbackListResponse getFeedbackPage(
            final Long organizationId,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackOrderBy orderBy
    ) {
        final Pageable pageable = Pageable.ofSize(size + 1);

        feedbackLikeService.flushLikeCountBuffer();
        
        final List<Feedback> feedbacks = feedBackRepository.findByOrganizationIdAndProcessStatusAndCursor(
                organizationId, cursorId, pageable, status, orderBy.name()
        );

        final FeedbackPage feedbackPage = FeedbackPage.createCursorPage(feedbacks, size);
        feedbackLikeCounter.applyBufferedLikeCount(feedbackPage);
        return AdminFeedbackListResponse.from(feedbackPage);
    }

    @Transactional
    @BusinessActionLog
    public UpdateFeedbackCommentResponse updateFeedbackComment(
            final UpdateFeedbackCommentRequest request,
            final Long feedbackId
    ) {
        final Feedback feedback = getFeedback(feedbackId);
        feedback.updateCommentAndStatus(request.toComment());
        return UpdateFeedbackCommentResponse.from(feedback);
    }

    private Feedback getFeedback(final Long feedbackId) {
        return feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + feedbackId + ")인 피드백을 찾을 수 없습니다."));
    }
}
