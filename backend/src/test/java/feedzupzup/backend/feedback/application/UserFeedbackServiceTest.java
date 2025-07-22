package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.place.domain.Place;
import feedzupzup.backend.place.domain.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserFeedbackServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private UserFeedbackService userFeedbackService;

    @Autowired
    private FeedBackRepository feedBackRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Nested
    @DisplayName("피드백 생성 테스트")
    class CreateFeedbackTest {

        @Test
        @DisplayName("피드백을 성공적으로 생성한다")
        void create_success() {
            //given
            final Place place = new Place("테스트장소", "테스트Url");
            final CreateFeedbackRequest request = new CreateFeedbackRequest("맛있어요", "https://example.com/image.jpg", false, "윌슨");

            //when
            final Place savedPlace = placeRepository.save(place);
            final CreateFeedbackResponse response = userFeedbackService.create(request, savedPlace.getId());

            //then
            assertAll(
                    () -> assertThat(response.feedbackId()).isNotNull(),
                    () -> assertThat(response.content()).isEqualTo(request.content()),
                    () -> assertThat(response.imageUrl()).isEqualTo(request.imageUrl()),
                    () -> assertThat(response.isSecret()).isEqualTo(request.isSecret()),
                    () -> assertThat(response.createdAt()).isNotNull()
            );
        }
    }

    @Nested
    @DisplayName("피드백 페이지 조회 테스트")
    class GetFeedbackPageTest {

        @Test
        @DisplayName("커서 기반 페이징으로 피드백 목록을 성공적으로 조회한다")
        void getFeedbackPage_success() {
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
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(placeId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(size),
                    () -> assertThat(response.hasNext()).isTrue(),
                    () -> assertThat(response.nextCursorId()).isNotNull()
            );
        }

        @Test
        @DisplayName("마지막 페이지에서 hasNext가 false를 반환한다")
        void getFeedbackPage_last_page() {
            // given
            final Long placeId = 1L;
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            
            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);

            final int size = 5;

            // when
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(placeId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.hasNext()).isFalse()
            );
        }

        @Test
        @DisplayName("빈 결과에 대해 적절히 처리한다")
        void getFeedbackPage_empty_result() {
            // given
            final Long placeId = 1L;
            final int size = 10;

            // when
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(placeId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).isEmpty(),
                    () -> assertThat(response.hasNext()).isFalse(),
                    () -> assertThat(response.nextCursorId()).isNull()
            );
        }

        @Test
        @DisplayName("특정 커서 ID로 다음 페이지를 조회한다")
        void getFeedbackPage_with_cursor() {
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
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(placeId, size, cursorId);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(saved2.getId()), // DESC 정렬이므로 saved2가 먼저
                    () -> assertThat(response.feedbacks().get(1).feedbackId()).isEqualTo(saved1.getId()),
                    () -> assertThat(response.hasNext()).isFalse()
            );
        }

        @Test
        @DisplayName("단일 페이지 결과에서 다음 페이지가 없음을 확인한다")
        void getFeedbackPage_single_page() {
            // given
            final Long placeId = 1L;
            final Feedback feedback = FeedbackFixture.createFeedbackWithPlaceId(placeId);
            feedBackRepository.save(feedback);

            final int size = 10;

            // when
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(placeId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(1),
                    () -> assertThat(response.hasNext()).isFalse(),
                    () -> assertThat(response.nextCursorId()).isNotNull()
            );
        }

        @Test
        @DisplayName("특정 장소의 피드백만 조회한다")
        void getFeedbackPage_only_specific_place() {
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
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(targetPlaceId, size, null);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.feedbacks())
                            .extracting(UserFeedbackListResponse.UserFeedbackItem::feedbackId)
                            .doesNotContain(otherFeedback.getId()),
                    () -> assertThat(response.hasNext()).isFalse()
            );
        }
    }
}
