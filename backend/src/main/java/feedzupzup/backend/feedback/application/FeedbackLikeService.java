package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedbackLikeInMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackLikeService {

    private final FeedbackLikeInMemoryRepository feedbackLikeInMemoryRepository;

    public void like(final Long feedbackId) {
        feedbackLikeInMemoryRepository.increase(feedbackId);
    }

    public void unLike(final Long feedbackId) {
        feedbackLikeInMemoryRepository.decrease(feedbackId);
    }
}
