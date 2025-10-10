package feedzupzup.backend.feedback.controller;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.feedback.application.FeedbackLikeService;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.util.CookieUtilization;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

class FeedbackLikeControllerE2ETest extends E2EHelper {

    @Autowired
    private FeedbackRepository feedBackRepository;

    @Autowired
    private FeedbackLikeService feedbackLikeService;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CookieUtilization cookieUtilization;

    @Test
    @DisplayName("피드백에 좋아요를 성공적으로 추가한다")
    void like_feedback_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                organizationCategory, 5);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then
        given()
                .log().all()
                .when()
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.afterLikeCount", equalTo(6));
    }

    @Test
    @DisplayName("이미 좋아요가 있는 피드백에 추가 좋아요를 성공적으로 추가한다")
    void like_feedback_with_existing_likes_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                organizationCategory, 10);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // 좋아요 2개 추가
        feedbackLikeService.like(savedFeedback.getId(), createGuest());
        feedbackLikeService.like(savedFeedback.getId(), createGuest());

        // when & then
        given()
                .log().all()
                .when()
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.afterLikeCount", equalTo(13));
    }

    @Test
    @DisplayName("해당 피드백에 대한 좋아요 기록이 없는 유저가 취소를 요청할 경우, 400 에러를 반환해야 한다")
    void not_exist_like_history_user_then_return_400() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                organizationCategory, 5);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        final UUID cookieValue = createAndGetCookieValue();

        // when & then
        given()
                .log().all()
                .when()
                .cookie(CookieUtilization.GUEST_KEY, cookieValue)
                .patch("/feedbacks/{feedbackId}/unlike", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("같은 피드백에 여러 번 좋아요를 추가한다")
    void like_feedback_multiple_times_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                organizationCategory, 0);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then - 첫 번째 좋아요
        given()
                .log().all()
                .when()
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(1));

        // when & then - 두 번째 좋아요
        given()
                .log().all()
                .when()
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(2));

        // when & then - 세 번째 좋아요
        given()
                .log().all()
                .when()
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(3));
    }

    @Test
    @DisplayName("좋아요 추가 후 취소하면 원래 상태로 돌아간다")
    void like_then_unlike_feedback_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                organizationCategory, 2);

        final Feedback savedFeedback = feedBackRepository.save(feedback);

        UUID guestId = UUID.randomUUID();

        // when - 좋아요 추가
        given()
                .log().all()
                .cookie(CookieUtilization.GUEST_KEY, guestId)
                .when()
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(3));

        // then - 좋아요 취소
        given()
                .log().all()
                .cookie(CookieUtilization.GUEST_KEY, guestId)
                .when()
                .patch("/feedbacks/{feedbackId}/unlike", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(2));
    }

    @Test
    @DisplayName("여러 번의 좋아요와 취소를 반복해도 정확한 카운트를 유지한다")
    void multiple_like_unlike_operations_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                organizationCategory, 0);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        final UUID cookieValue1 = createAndGetCookieValue();
        final UUID cookieValue2 = createAndGetCookieValue();
        final UUID cookieValue3 = createAndGetCookieValue();


        // when & then - 좋아요 추가
        given()
                .when()
                .cookie(CookieUtilization.GUEST_KEY, cookieValue1)
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(1));

        // when & then - 좋아요 추가
        given()
                .when()
                .cookie(CookieUtilization.GUEST_KEY, cookieValue2)
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(2));

        // when & then - 좋아요 취소
        given()
                .when()
                .cookie(CookieUtilization.GUEST_KEY, cookieValue1)
                .patch("/feedbacks/{feedbackId}/unlike", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(1));

        // when & then - 좋아요 추가
        given()
                .when()
                .cookie(CookieUtilization.GUEST_KEY, cookieValue3)
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(2));

        // when & then - 좋아요 취소
        given()
                .when()
                .cookie(CookieUtilization.GUEST_KEY, cookieValue2)
                .patch("/feedbacks/{feedbackId}/unlike", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(1));
    }

    @Test
    @DisplayName("유효한 피드백 ID에 정상적으로 좋아요가 동작한다")
    void like_valid_feedback_id_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                organizationCategory, 0);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then
        given()
                .log().all()
                .when()
                .patch("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.afterLikeCount", equalTo(1));
    }

    private UUID createAndGetCookieValue() {
        final ResponseCookie cookie = cookieUtilization.createCookie(CookieUtilization.GUEST_KEY,
                UUID.randomUUID());
        return UUID.fromString(cookie.getValue());
    }

    private Guest createGuest() {
        return new Guest(UUID.randomUUID(), CurrentDateTime.create());
    }
}
