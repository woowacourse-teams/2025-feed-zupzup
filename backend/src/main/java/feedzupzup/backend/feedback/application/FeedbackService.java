package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedBackRepository feedBackRepository;

    @Transactional
    public CreateFeedbackResponse create(final CreateFeedbackRequest request, final Long placeId) {
        final Feedback newFeedback = request.toFeedback(placeId);
        final Feedback savedFeedback = feedBackRepository.save(newFeedback);
        return CreateFeedbackResponse.from(savedFeedback);
    }

}
