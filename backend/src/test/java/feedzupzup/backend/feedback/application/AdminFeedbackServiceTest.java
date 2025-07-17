package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackStatusResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
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
            final Feedback feedback = FeedbackFixture.createFeedbackWithPlaceId(1L);
            final Feedback savedFeedback = feedBackRepository.save(feedback);

            // when
            adminFeedbackService.delete(savedFeedback.getId());

            // then
            assertThat(feedBackRepository.findById(savedFeedback.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 피드백 삭제 시 예외가 발생한다")
        void delete_non_existing_feedback_exception() {
            // given
            final Long nonExistingId = 999L;

            // when & then - 예외가 발생해야 함
            assertThatThrownBy(() -> adminFeedbackService.delete(nonExistingId))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("피드백 상태 업데이트 테스트")
    class UpdateFeedbackStatusTest {

        @Test
        @DisplayName("유효한 피드백 ID와 상태로 업데이트 시 성공한다")
        void updateFeedbackStatus_success() {
            // given
            final Feedback feedback = FeedbackFixture.createFeedbackWithStatus(ProcessStatus.WAITING);
            final Feedback savedFeedback = feedBackRepository.save(feedback);
            final UpdateFeedbackStatusRequest request = new UpdateFeedbackStatusRequest(ProcessStatus.CONFIRMED);

            // when
            final UpdateFeedbackStatusResponse response = adminFeedbackService.updateFeedbackStatus(
                    request, savedFeedback.getId());

            // then
            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(ProcessStatus.CONFIRMED);

            final Feedback updatedFeedback = feedBackRepository.findById(savedFeedback.getId()).orElseThrow();
            assertThat(updatedFeedback.getStatus()).isEqualTo(ProcessStatus.CONFIRMED);
        }

        @Test
        @DisplayName("존재하지 않는 피드백 ID로 업데이트 시 예외가 발생한다")
        void updateFeedbackStatus_not_found() {
            // given
            final Long nonExistentFeedbackId = 999L;
            final UpdateFeedbackStatusRequest request = new UpdateFeedbackStatusRequest(ProcessStatus.CONFIRMED);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.updateFeedbackStatus(
                    request, nonExistentFeedbackId))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
