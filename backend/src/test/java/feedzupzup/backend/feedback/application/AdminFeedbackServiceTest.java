package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminFeedbackServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private AdminFeedbackService adminFeedbackService;

    @Autowired
    private FeedBackRepository feedBackRepository;

    @Nested
    @DisplayName("피드백 삭제 테스트")
    class DeleteFeedbackTest {

        @Test
        @DisplayName("피드백을 성공적으로 삭제한다")
        void delete_success() {
            // given
            final Feedback feedback = Feedback.builder()
                    .content("삭제될 피드백")
                    .isSecret(false)
                    .status(ProcessStatus.WATING)
                    .placeId(1L)
                    .imageUrl("https://example.com/image.jpg")
                    .build();
            final Feedback savedFeedback = feedBackRepository.save(feedback);

            // when
            adminFeedbackService.delete(savedFeedback.getId());

            // then
            assertThat(feedBackRepository.findById(savedFeedback.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 피드백 삭제 시 예외가 발생하지 않는다")
        void delete_non_existing_feedback_no_exception() {
            // given
            final Long nonExistingId = 999L;

            // when & then - 예외가 발생하지 않아야 함
            assertThatCode(() -> adminFeedbackService.delete(nonExistingId))
                    .doesNotThrowAnyException();
        }
    }
}
