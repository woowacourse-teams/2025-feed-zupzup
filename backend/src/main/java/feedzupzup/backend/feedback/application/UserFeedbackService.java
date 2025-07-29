package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeCounter;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.group.domain.Group;
import feedzupzup.backend.group.domain.GroupRepository;
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
    private final GroupRepository groupRepository;

    @Transactional
    public CreateFeedbackResponse create(final CreateFeedbackRequest request, final Long groupId) {
        final Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("장소를 찾을 수 없습니다."));
        final Feedback newFeedback = request.toFeedback(group.getId());
        final Feedback savedFeedback = feedBackRepository.save(newFeedback);
        return CreateFeedbackResponse.from(savedFeedback);
    }

    public UserFeedbackListResponse getFeedbackPage(
            final Long groupId,
            final int size,
            final Long cursorId
    ) {
        final Pageable pageable = Pageable.ofSize(size + 1);
        final List<Feedback> feedbacks = feedBackRepository.findPageByGroupIdAndCursorIdOrderByDesc(
                groupId,
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
}
