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

    private void saveUserIfNotExist(final UUID uuid) {
        userLikeFeedbacks.computeIfAbsent(uuid, key -> new LikeFeedbacks());
    }

    public Map<UUID, LikeFeedbacks> getUserLikeFeedbacks() {
        return Collections.unmodifiableMap(userLikeFeedbacks);
    }
}
