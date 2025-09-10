package feedzupzup.backend.feedback.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserLikeFeedbacksRepository {

    private final Map<UUID, LikeFeedbacks> userLikeFeedbacks = new HashMap<>();

    public void save(UUID uuid, int feedbackId) {

        final LikeFeedbacks likeFeedbacks = userLikeFeedbacks.computeIfAbsent(uuid,
                key -> new LikeFeedbacks());
        likeFeedbacks.add(feedbackId);
    }

    public boolean isAlreadyLike(final UUID uuid, final int feedbackId) {
        final LikeFeedbacks likeFeedbacks = userLikeFeedbacks.get(uuid);
        return likeFeedbacks.hasFeedbackId(feedbackId);
    }

    public Map<UUID, LikeFeedbacks> getUserLikeFeedbacks() {
        return userLikeFeedbacks;
    }
}
