package feedzupzup.backend.feedback.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserLikeFeedbacksRepositoryTest {

    private final UserLikeFeedbacksRepository userLikeFeedbacksRepository = new UserLikeFeedbacksRepository();

    @DisplayName("좋아요를 누를 때, feedbackId가 성공적으로 저장되어야 한다")
    @Test
    void when_like_then_save_feedbackId() {
        // when
        userLikeFeedbacksRepository.save(UUID.randomUUID(), 1);

        // then
        assertThat(userLikeFeedbacksRepository.getUserLikeFeedbacks().size()).isEqualTo(1);
    }

    @DisplayName("서로 다른 feedbackId에 대해 좋아요를 3번 누른다면, feedbackId 3개가 성공적으로 저장되어야 한다")
    @Test
    void when_like_then_save_feedbackId_multiple_case() {
        // when
        final UUID uuid = UUID.randomUUID();

        userLikeFeedbacksRepository.save(uuid, 1);
        userLikeFeedbacksRepository.save(uuid, 2);
        userLikeFeedbacksRepository.save(uuid, 3);

        // then
        assertThat(userLikeFeedbacksRepository.getUserLikeFeedbacks().get(uuid).getLikeFeedbacks().size()).isEqualTo(3);
    }

}
