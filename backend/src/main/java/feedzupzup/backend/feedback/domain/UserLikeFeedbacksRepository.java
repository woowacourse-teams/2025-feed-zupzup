package feedzupzup.backend.feedback.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserLikeFeedbacksRepository {

    private final Map<UUID, LikeFeedbacks> userLikeFeedbacks = new HashMap<>();

    public void save(UUID uuid, long feedbackId) {
        saveUserIfNotExist(uuid);
        final LikeFeedbacks likeFeedbacks = userLikeFeedbacks.get(uuid);
        likeFeedbacks.add(feedbackId);
    }

    public void deleteLikeHistory(UUID uuid, long feedbackId) {
        final LikeFeedbacks likeFeedbacks = userLikeFeedbacks.get(uuid);
        likeFeedbacks.remove(feedbackId);
    }

    private void saveUserIfNotExist(final UUID uuid) {
        userLikeFeedbacks.computeIfAbsent(uuid, key -> new LikeFeedbacks());
    }

    public boolean isAlreadyLike(final UUID uuid, final long feedbackId) {
        final LikeFeedbacks likeFeedbacks = userLikeFeedbacks.get(uuid);
        if (likeFeedbacks == null) {
            return false;
        }
        return likeFeedbacks.hasFeedbackId(feedbackId);
    }

    public Map<UUID, LikeFeedbacks> getUserLikeFeedbacks() {
        return userLikeFeedbacks;
    }
}
