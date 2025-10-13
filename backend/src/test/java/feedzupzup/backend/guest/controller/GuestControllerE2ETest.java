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
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class GuestControllerE2ETest extends E2EHelper {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CookieUtilization cookieUtilization;

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

}
