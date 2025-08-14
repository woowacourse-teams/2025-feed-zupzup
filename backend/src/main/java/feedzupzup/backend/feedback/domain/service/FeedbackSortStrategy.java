package feedzupzup.backend.feedback.domain.service;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortBy;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface FeedbackSortStrategy {

    List<Feedback> getSortedFeedbacks(final Long organizationId, final ProcessStatus status, final Long cursorId,
            final Pageable pageable);

    FeedbackSortBy getType();

    List<Feedback> sort(List<Feedback> feedbacks);
}
