package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackSecretResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackStatusResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
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
        @DisplayName("존재하지 않는 피드백 삭제 시 예외가 발생 하지 않는다.")
        void delete_non_existing_feedback_exception() {
            // given
            final Long nonExistingId = 999L;

            // when & then
            assertThatCode(() -> adminFeedbackService.delete(nonExistingId)).doesNotThrowAnyException();
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
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("피드백 비밀상태 변경 테스트")
    class UpdateFeedbackSecretTest {

        @Test
        @DisplayName("유효한 피드백 ID와 비밀상태로 업데이트 시 성공한다")
        void updateFeedbackSecret_success() {
            // given
            final Feedback feedback = FeedbackFixture.createFeedbackWithSecret(false);
            final Feedback savedFeedback = feedBackRepository.save(feedback);
            final UpdateFeedbackSecretRequest request = new UpdateFeedbackSecretRequest(true);

            // when
            final UpdateFeedbackSecretResponse response = adminFeedbackService.updateFeedbackSecret(
                    savedFeedback.getId(), request);

            // then
            assertAll(
                    () -> assertThat(response.feedbackId()).isEqualTo(savedFeedback.getId()),
                    () -> assertThat(response.isSecret()).isTrue()
            );
        }

        @Test
        @DisplayName("존재하지 않는 피드백 ID로 비밀상태 변경 시 예외가 발생한다")
        void updateFeedbackSecret_not_found() {
            // given
            final Long nonExistentFeedbackId = 999L;
            final UpdateFeedbackSecretRequest request = new UpdateFeedbackSecretRequest(true);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.updateFeedbackSecret(nonExistentFeedbackId, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("전체 피드백 조회 테스트")
    class GetAllFeedbacksTest {

        @Test
        @DisplayName("커서 기반 페이징으로 피드백 목록을 성공적으로 조회한다")
        void getAllFeedbacks_success() {
            // given
            final Long placeId = 1L;
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            
            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);
            feedBackRepository.save(feedback4);

            final int size = 2;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(placeId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(size),
                    () -> assertThat(response.hasNext()).isTrue(),
                    () -> assertThat(response.nextCursorId()).isNotNull()
            );
        }

        @Test
        @DisplayName("마지막 페이지에서 hasNext가 false를 반환한다")
        void getAllFeedbacks_last_page() {
            // given
            final Long placeId = 1L;
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            
            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);

            final int size = 5;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(placeId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.hasNext()).isFalse()
            );
        }

        @Test
        @DisplayName("빈 결과에 대해 적절히 처리한다")
        void getAllFeedbacks_empty_result() {
            // given
            final Long placeId = 999L;
            final int size = 10;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(placeId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).isEmpty(),
                    () -> assertThat(response.hasNext()).isFalse(),
                    () -> assertThat(response.nextCursorId()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("특정 장소 피드백 조회 테스트")
    class GetFeedbackPageByPlaceIdTest {

        @Test
        @DisplayName("특정 장소의 피드백만 조회한다")
        void getFeedbackPageByPlaceId_success() {
            // given
            final Long targetPlaceId = 1L;
            final Long otherPlaceId = 2L;
            
            final Feedback targetFeedback1 = FeedbackFixture.createFeedbackWithPlaceId(targetPlaceId);
            final Feedback targetFeedback2 = FeedbackFixture.createFeedbackWithPlaceId(targetPlaceId);
            final Feedback otherFeedback = FeedbackFixture.createFeedbackWithPlaceId(otherPlaceId);
            
            feedBackRepository.save(targetFeedback1);
            feedBackRepository.save(targetFeedback2);
            feedBackRepository.save(otherFeedback);

            final int size = 10;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(targetPlaceId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.feedbacks())
                            .extracting(AdminFeedbackListResponse.AdminFeedbackItem::feedbackId)
                            .doesNotContain(otherFeedback.getId()),
                    () -> assertThat(response.hasNext()).isFalse()
            );
        }

        @Test
        @DisplayName("특정 장소의 피드백을 커서 기반 페이징으로 조회한다")
        void getFeedbackPageByPlaceId_with_paging() {
            // given
            final Long placeId = 1L;
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            
            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);
            feedBackRepository.save(feedback4);

            final int size = 2;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(placeId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(size),
                    () -> assertThat(response.hasNext()).isTrue(),
                    () -> assertThat(response.nextCursorId()).isNotNull()
            );
        }

        @Test
        @DisplayName("특정 장소의 피드백이 없을 때 빈 결과를 반환한다")
        void getFeedbackPageByPlaceId_empty_result() {
            // given
            final Long emptyPlaceId = 999L;
            final int size = 10;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(emptyPlaceId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).isEmpty(),
                    () -> assertThat(response.hasNext()).isFalse(),
                    () -> assertThat(response.nextCursorId()).isNull()
            );
        }

        @Test
        @DisplayName("특정 장소의 피드백을 커서 ID와 함께 조회한다")
        void getFeedbackPageByPlaceId_with_cursor() {
            // given
            final Long placeId = 1L;
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            
            final Feedback saved1 = feedBackRepository.save(feedback1);
            final Feedback saved2 = feedBackRepository.save(feedback2);
            final Feedback saved3 = feedBackRepository.save(feedback3);
            final Feedback saved4 = feedBackRepository.save(feedback4);

            final int size = 2;
            final Long cursorId = saved3.getId(); // saved3를 커서로 사용하면 saved2, saved1이 반환됨

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(placeId, size, cursorId);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(saved2.getId()), // DESC 정렬이므로 saved2가 먼저
                    () -> assertThat(response.feedbacks().get(1).feedbackId()).isEqualTo(saved1.getId()),
                    () -> assertThat(response.hasNext()).isFalse()
            );
        }
    }
}
