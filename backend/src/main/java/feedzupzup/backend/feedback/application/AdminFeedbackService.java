package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminFeedbackService {

    private final FeedBackRepository feedBackRepository;

    public void delete(final Long feedbackId) {
        feedBackRepository.deleteById(feedbackId);
    }
}
