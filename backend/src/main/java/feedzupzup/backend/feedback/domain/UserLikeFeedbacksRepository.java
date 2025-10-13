package feedzupzup.backend.feedback.domain;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class UserLikeFeedbacksRepository {

    private final Map<UUID, LikeFeedbacks> userLikeFeedbacks = new ConcurrentHashMap<>();

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
        return Collections.unmodifiableMap(userLikeFeedbacks);
    }

    public LikeFeedbacks getUserLikeFeedbacksFrom(final UUID guestId) {
        return Optional.ofNullable(userLikeFeedbacks.get(guestId))
                .orElse(new LikeFeedbacks());
    }
}
