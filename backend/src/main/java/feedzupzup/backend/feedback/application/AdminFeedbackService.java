package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackSecretResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackStatusResponse;
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

    @Transactional
    public void delete(final Long feedbackId) {
        feedBackRepository.deleteById(feedbackId);
    }

    @Transactional
    public UpdateFeedbackStatusResponse updateFeedbackStatus(
            final UpdateFeedbackStatusRequest request,
            final Long feedbackId
    ) {
        final Feedback feedBack = feedBackRepository.findById(feedbackId)
                .orElseThrow(IllegalArgumentException::new);
        feedBack.updateStatus(request.status());
        return UpdateFeedbackStatusResponse.from(feedBack);
    }

    @Transactional
    public UpdateFeedbackSecretResponse updateFeedbackSecret(
            final Long feedbackId,
            final UpdateFeedbackSecretRequest request
    ) {
        final Feedback feedBack = feedBackRepository.findById(feedbackId)
                .orElseThrow(IllegalArgumentException::new);
        feedBack.updateSecret(request.isSecret());
        return UpdateFeedbackSecretResponse.from(feedBack);
    }

    public AdminFeedbackListResponse getFeedbackPage(final int size, final Long cursorId) {
        final Pageable pageable = Pageable.ofSize(size + 1);
        final List<Feedback> feedbacks = feedBackRepository.findPageByCursorIdOrderByDesc(cursorId, pageable);
        final FeedbackPage feedbackPage = FeedbackPage.createCursorPage(feedbacks, size);
        return AdminFeedbackListResponse.from(feedbackPage);
    }
}
