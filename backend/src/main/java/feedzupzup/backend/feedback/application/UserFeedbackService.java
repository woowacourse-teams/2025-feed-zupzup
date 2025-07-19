package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
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

    @Transactional
    public CreateFeedbackResponse create(final CreateFeedbackRequest request, final Long placeId) {
        final Feedback newFeedback = request.toFeedback(placeId);
        final Feedback savedFeedback = feedBackRepository.save(newFeedback);
        return CreateFeedbackResponse.from(savedFeedback);
    }

    public UserFeedbackListResponse getFeedbackPage(
            final Long placeId,
            final int size,
            final Long cursorId
    ) {
        final Pageable pageable = Pageable.ofSize(size + 1);
        final List<Feedback> feedbacks = feedBackRepository.findPageByPlaceIdAndCursorIdOrderByDesc(placeId, cursorId, pageable);
        final FeedbackPage feedbackPage = FeedbackPage.createCursorPage(feedbacks, size);
        return UserFeedbackListResponse.from(feedbackPage);
    }
}
