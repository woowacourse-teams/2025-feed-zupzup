package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedbackLikeInMemoryRepository;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackLikeService {

    private final FeedbackLikeInMemoryRepository feedbackLikeInMemoryRepository;

    public LikeResponse like(final Long feedbackId) {
        return new LikeResponse(feedbackLikeInMemoryRepository.increase(feedbackId));
    }

    public LikeResponse unLike(final Long feedbackId) {
        return new LikeResponse(feedbackLikeInMemoryRepository.decrease(feedbackId));
    }
}
