package feedzupzup.backend.guest.controller;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.feedback.fixture.FeedbackRequestFixture;
import feedzupzup.backend.global.util.CookieUtilization;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class GuestControllerE2ETest extends E2EHelper {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private GuestRepository guestRepository;

    private Organization organization;
    private OrganizationCategory organizationCategory;

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);
    }

    @Nested
    @DisplayName("좋아요 목록 조회 테스트")
    class LikeHistory {

        @Test
        @DisplayName("본인이 좋아요 누른 피드백 목록을 조회할 수 있어야 한다")
        void get_like_histories() {
            // given
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedbackRepository.save(feedback);

            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // when
            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my-likes",
                            organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.feedbackIds[0]", is(savedFeedback.getId().intValue()));
        }

        @Test
        @DisplayName("본인이 좋아요 누른 피드백 목록들을 전부 조회할 수 있어야 한다")
        void get_like_histories_multiple_case() {
            // given
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);

            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);

            final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);

            final Feedback savedFeedback1 = feedbackRepository.save(feedback1);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);
            final Feedback savedFeedback3 = feedbackRepository.save(feedback3);

            // 첫 번째 좋아요로 쿠키 발급받기
            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback1.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback2.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback3.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // when
            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my-likes",
                            organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.feedbackIds", hasSize(3))
                    .body("data.feedbackIds[0]", is(savedFeedback1.getId().intValue()))
                    .body("data.feedbackIds[1]", is(savedFeedback2.getId().intValue()))
                    .body("data.feedbackIds[2]", is(savedFeedback3.getId().intValue()));
        }

        @Test
        @DisplayName("좋아요 목록 조회 시, 쿠키가 없다면, 빈 배열이 반환되어야 한다")
        void get_like_histories_none_cookie_case() {
            // when
            given()
                    .log().all()
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my-likes",
                            organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.feedbackIds", empty());
        }
    }

    @Nested
    @DisplayName("내가 쓴 글 조회 테스트")
    class MyFeedback {

        @Test
        @DisplayName("본인이 작성한 피드백 목록들을 전부 조회할 수 있어야 한다")
        void get_my_feedbacks_multiple_case() {
            // given
            final CreateFeedbackRequest request = FeedbackRequestFixture.createRequestWithContent("피드백");

            // 첫 번째 게시글 작성
            final String visitorCookie = given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // 두 번째 게시글 작성
            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value());

            // when & then
            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my",
                            organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.feedbacks", hasSize(2));
        }

        @Test
        @DisplayName("내가 쓴 글 조회 시, 쿠키가 없다면, 빈 배열이 반환되어야 한다")
        void get_my_feedbacks_none_cookie_case() {
            // when & then
            given()
                    .log().all()
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my", organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.feedbacks", empty());
        }
    }

    @Nested
    @DisplayName("GuestInterceptor 동작 테스트")
    class GuestInterceptorBehavior {

        @Test
        @DisplayName("쿠키가 없을 때 @VisitedGuest 엔드포인트 호출 시 쿠키가 생성되고 DB에 저장되지 않아야 한다")
        void visited_guest_creates_cookie_without_db_save() {
            // when
            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my-likes",
                            organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // then
            assertThat(visitorCookie).isNotNull();
            final UUID guestUuid = UUID.fromString(visitorCookie);
            assertThat(guestRepository.existsByGuestUuid(guestUuid)).isFalse();
        }

        @Test
        @DisplayName("쿠키가 없을 때 @SavedGuest 엔드포인트 호출 시 쿠키가 생성되고 DB에 저장되어야 한다")
        void saved_guest_creates_cookie_with_db_save() {
            // given
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedbackRepository.save(feedback);

            // when
            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // then
            assertThat(visitorCookie).isNotNull();
            final UUID guestUuid = UUID.fromString(visitorCookie);
            assertThat(guestRepository.existsByGuestUuid(guestUuid)).isTrue();
        }

        @Test
        @DisplayName("@VisitedGuest로 생성된 쿠키로 @SavedGuest 엔드포인트 호출 시 DB에 저장되어야 한다")
        void visited_guest_cookie_saved_when_calling_saved_guest_endpoint() {
            // given - @VisitedGuest 엔드포인트로 쿠키 생성
            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my-likes",
                            organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            final UUID guestUuid = UUID.fromString(visitorCookie);
            assertThat(guestRepository.existsByGuestUuid(guestUuid)).isFalse();

            // when - @SavedGuest 엔드포인트 호출
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedbackRepository.save(feedback);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // then - DB에 저장되어야 함
            assertThat(guestRepository.existsByGuestUuid(guestUuid)).isTrue();
        }

        @Test
        @DisplayName("@SavedGuest로 생성된 쿠키로 @SavedGuest 엔드포인트 재호출 시 중복 저장되지 않아야 한다")
        void saved_guest_not_duplicated() {
            // given - @SavedGuest 엔드포인트로 쿠키 생성 및 DB 저장
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback1 = feedbackRepository.save(feedback1);

            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback1.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            final UUID guestUuid = UUID.fromString(visitorCookie);
            final long initialCount = guestRepository.count();

            // when - 동일한 쿠키로 @SavedGuest 엔드포인트 재호출
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);

            given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, visitorCookie)
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback2.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value());

            // then - 중복 저장되지 않아야 함
            assertThat(guestRepository.count()).isEqualTo(initialCount);
            assertThat(guestRepository.existsByGuestUuid(guestUuid)).isTrue();
        }

        @Test
        @DisplayName("모든 Guest 엔드포인트 호출 시 쿠키가 갱신되어야 한다")
        void cookie_should_be_renewed_on_every_request() {
            // given
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedbackRepository.save(feedback);

            // when & then - @SavedGuest 엔드포인트
            final String cookie1 = given()
                    .log().all()
                    .when()
                    .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            assertThat(cookie1).isNotNull();

            // @VisitedGuest 엔드포인트로 쿠키 갱신 확인
            final String cookie2 = given()
                    .log().all()
                    .cookie(CookieUtilization.GUEST_KEY, cookie1)
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my-likes",
                            organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            assertThat(cookie2).isNotNull();
            assertThat(cookie2).isEqualTo(cookie1); // 같은 UUID여야 함
        }

        @Test
        @DisplayName("@SavedGuest 피드백 생성 API로 쿠키 생성 및 DB 저장 확인")
        void saved_guest_feedback_creation_saves_to_db() {
            // given
            final CreateFeedbackRequest request = FeedbackRequestFixture.createRequestWithContent("테스트 피드백");

            // when
            final String visitorCookie = given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // then
            assertThat(visitorCookie).isNotNull();
            final UUID guestUuid = UUID.fromString(visitorCookie);
            assertThat(guestRepository.existsByGuestUuid(guestUuid)).isTrue();
        }

        @Test
        @DisplayName("@VisitedGuest 내가 쓴 글 조회 API로 쿠키 생성 시 DB에 저장되지 않아야 한다")
        void visited_guest_my_feedbacks_does_not_save_to_db() {
            // when
            final String visitorCookie = given()
                    .log().all()
                    .when()
                    .get("/organizations/{organizationUuid}/feedbacks/my",
                            organization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .cookie(CookieUtilization.GUEST_KEY);

            // then
            assertThat(visitorCookie).isNotNull();
            final UUID guestUuid = UUID.fromString(visitorCookie);
            assertThat(guestRepository.existsByGuestUuid(guestUuid)).isFalse();
        }
    }

}
