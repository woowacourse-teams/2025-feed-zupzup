package feedzupzup.backend.feedback.domain;

import java.util.Map;

public interface FeedbackLikeRepository {

    int increaseAndGet(Long feedbackId);

    int decreaseAndGet(Long feedbackId);

    int getLikeCount(Long feedbackId);

    Map<Long, Integer> getLikeCounts();

    void clear();
}
