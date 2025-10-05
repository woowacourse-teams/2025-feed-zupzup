package feedzupzup.backend.feedback.domain.service.sort;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface FeedbackSortStrategy {

    List<FeedbackItem> getSortedFeedbacks(final UUID organizationUuId, final ProcessStatus status, final Long cursorId,
            final Pageable pageable);

    FeedbackSortType getType();

    List<FeedbackItem> sort(final List<Feedback> feedbacks);

}
