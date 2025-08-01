package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeCounter;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
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

    private final FeedBackRepository feedBackRepository;
    private final FeedbackLikeCounter feedbackLikeCounter;

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
        final Feedback feedback = feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + feedbackId + ")인 피드백을 찾을 수 없습니다."));
        feedback.updateStatus(request.status());
        return UpdateFeedbackStatusResponse.from(feedback);
    }

    @Transactional
    @BusinessActionLog
    public UpdateFeedbackSecretResponse updateFeedbackSecret(
            final Long feedbackId,
            final UpdateFeedbackSecretRequest request
    ) {
        final Feedback feedBack = feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + feedbackId + ")인 피드백을 찾을 수 없습니다."));
        feedBack.updateSecret(request.isSecret());
        return UpdateFeedbackSecretResponse.from(feedBack);
    }

    public AdminFeedbackListResponse getFeedbackPage(
            final Long organizationId,
            final int size,
            final Long cursorId
    ) {
        final Pageable pageable = Pageable.ofSize(size + 1);
        final List<Feedback> feedbacks = feedBackRepository.findPageByOrganizationIdAndCursorIdOrderByDesc(organizationId, cursorId, pageable);
        final FeedbackPage feedbackPage = FeedbackPage.createCursorPage(feedbacks, size);
        feedbackLikeCounter.applyBufferedLikeCount(feedbackPage);
        return AdminFeedbackListResponse.from(feedbackPage);
    }
}
