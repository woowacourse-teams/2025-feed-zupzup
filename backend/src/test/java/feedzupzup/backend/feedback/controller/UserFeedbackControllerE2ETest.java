package feedzupzup.backend.feedback.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.feedback.fixture.FeedbackRequestFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class UserFeedbackControllerE2ETest extends E2EHelper {

    @Autowired
    private FeedBackRepository feedBackRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("사용자가 특정 장소의 피드백 목록을 성공적으로 조회한다")
    void user_get_feedbacks_success() {
        // given
        final Long organizationId = 1L;
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .when()
                .get("/organizations/{organizationId}/feedbacks", organizationId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(3))
                .body("data.hasNext", equalTo(false))
                .body("data.nextCursorId", notNullValue());
    }

    @Test
    @DisplayName("사용자가 커서 기반 페이징으로 피드백 목록을 조회한다")
    void user_get_feedbacks_with_cursor_pagination() {
        // given
        final Long organizationId = 1L;
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);

        // when - 첫 번째 페이지 조회
        final Long firstPageCursor = given()
                .log().all()
                .queryParam("size", 2)
                .when()
                .get("/organizations/{organizationId}/feedbacks", organizationId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(2))
                .body("data.hasNext", equalTo(true))
                .extract()
                .jsonPath()
                .getLong("data.nextCursorId");

        // when - 두 번째 페이지 조회
        given()
                .log().all()
                .queryParam("size", 2)
                .queryParam("cursorId", firstPageCursor)
                .when()
                .get("/organizations/{organizationId}/feedbacks", organizationId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(1))
                .body("data.hasNext", equalTo(false));
    }

    @Test
    @DisplayName("사용자가 빈 피드백 목록을 조회한다")
    void user_get_empty_feedbacks() {
        // given
        final Long organizationId = 999L; // 피드백이 없는 장소

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .when()
                .get("/organizations/{organizationId}/feedbacks", organizationId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(0))
                .body("data.hasNext", equalTo(false))
                .body("data.nextCursorId", equalTo(null));
    }

    @Test
    @DisplayName("사용자가 특정 장소의 피드백만 조회한다 (다른 장소 피드백 제외)")
    void user_get_feedbacks_only_for_specific_organization() {
        // given
        final Long targetOrganizationId = 1L;
        final Long otherOrganizationId = 2L;
        
        final Feedback targetFeedback1 = FeedbackFixture.createFeedbackWithOrganizationId(targetOrganizationId);
        final Feedback targetFeedback2 = FeedbackFixture.createFeedbackWithOrganizationId(targetOrganizationId);
        final Feedback otherFeedback = FeedbackFixture.createFeedbackWithOrganizationId(otherOrganizationId);

        feedBackRepository.save(targetFeedback1);
        feedBackRepository.save(targetFeedback2);
        feedBackRepository.save(otherFeedback);

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .when()
                .get("/organizations/{organizationId}/feedbacks", targetOrganizationId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(2)) // 대상 장소의 피드백 2개만
                .body("data.hasNext", equalTo(false));
    }

    @Test
    @DisplayName("피드백을 성공적으로 생성한다")
    void create_feedback_success() {
        // given
        final Organization organization = new Organization("테스트장소");
        final Organization savedOrganization = organizationRepository.save(organization);
        final CreateFeedbackRequest request = FeedbackRequestFixture.createRequestWithContent("피드백");

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/organizations/{organizationId}/feedbacks", savedOrganization.getId())
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(201))
                .body("message", equalTo("CREATED"))
                .body("data.feedbackId", notNullValue())
                .body("data.content", equalTo("피드백"))
                .body("data.isSecret", equalTo(false))
                .body("data.createdAt", notNullValue());
    }

    @Test
    @DisplayName("사용자가 비밀 피드백을 성공적으로 생성한다")
    void user_create_secret_feedback_success() {
        // given
        final Organization organization = new Organization("테스트장소");
        final Organization savedOrganization = organizationRepository.save(organization);
        final CreateFeedbackRequest request = new CreateFeedbackRequest("비밀 피드백입니다", true, "테스트유저");

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/organizations/{organizationId}/feedbacks", savedOrganization.getId())
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(201))
                .body("message", equalTo("CREATED"))
                .body("data.feedbackId", notNullValue())
                .body("data.content", equalTo("비밀 피드백입니다"))
                .body("data.isSecret", equalTo(true))
                .body("data.createdAt", notNullValue());
    }

    @Test
    @DisplayName("사용자가 새로 생성한 피드백이 목록에 나타난다")
    void user_create_feedback_appears_in_list() {
        // given
        final Organization organization = new Organization("테스트장소");
        final Organization savedOrganization = organizationRepository.save(organization);
        final CreateFeedbackRequest request = new CreateFeedbackRequest("새 피드백", false, "테스트유저");

        // when - 피드백 생성
        final Long createdFeedbackId = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/organizations/{organizationId}/feedbacks", savedOrganization.getId())
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .getLong("data.feedbackId");

        // then - 목록에서 확인
        given()
                .log().all()
                .queryParam("size", 10)
                .when()
                .get("/organizations/{organizationId}/feedbacks", savedOrganization.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(greaterThan(0)))
                .body("data.feedbacks[0].feedbackId", equalTo(createdFeedbackId.intValue()))
                .body("data.feedbacks[0].content", equalTo("새 피드백"))
                .body("data.feedbacks[0].isSecret", equalTo(false));
    }
}
