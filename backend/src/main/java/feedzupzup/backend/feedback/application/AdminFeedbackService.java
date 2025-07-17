package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackStatusResponse;
import lombok.RequiredArgsConstructor;
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
}
