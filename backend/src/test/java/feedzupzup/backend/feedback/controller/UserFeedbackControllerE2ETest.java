package feedzupzup.backend.feedback.controller;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.CONFIRMED;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.feedback.fixture.FeedbackRequestFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class UserFeedbackControllerE2ETest extends E2EHelper {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    Organization organization;
    OrganizationCategory organizationCategory;

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);
    }

    @Test
    @DisplayName("사용자가 특정 장소의 피드백 목록을 성공적으로 조회한다")
    void user_get_feedbacks_success() {
        // given
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);

        feedbackRepository.save(feedback1);
        feedbackRepository.save(feedback2);
        feedbackRepository.save(feedback3);

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
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
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);

        feedbackRepository.save(feedback1);
        feedbackRepository.save(feedback2);
        feedbackRepository.save(feedback3);

        // when - 첫 번째 페이지 조회
        final Long firstPageCursor = given()
                .log().all()
                .queryParam("size", 2)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
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
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
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
        final UUID organizationUuid = UUID.randomUUID(); // 피드백이 없는 장소

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/organizations/{organizationUuid}/feedbacks", organizationUuid)
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
        final Organization organization2 = OrganizationFixture.createAllBlackBox();

        organizationRepository.save(organization2);

        final OrganizationCategory organizationCategory2 = OrganizationCategoryFixture.createOrganizationCategory(
                organization2, SUGGESTION);

        organizationCategoryRepository.save(organizationCategory);
        organizationCategoryRepository.save(organizationCategory2);

        final Feedback targetFeedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback targetFeedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback otherFeedback = FeedbackFixture.createFeedbackWithOrganization(organization2,
                organizationCategory2);

        feedbackRepository.save(targetFeedback1);
        feedbackRepository.save(targetFeedback2);
        feedbackRepository.save(otherFeedback);

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
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
        final CreateFeedbackRequest request = FeedbackRequestFixture.createRequestWithContent("피드백");
        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(201))
                .body("message", equalTo("CREATED"))
                .body("data.feedbackId", notNullValue())
                .body("data.content", equalTo("피드백"))
                .body("data.isSecret", equalTo(false))
                .body("data.postedAt", notNullValue())
                .body("data.category", equalTo("건의"));
    }

    @Test
    @DisplayName("사용자가 비밀 피드백을 성공적으로 생성한다")
    void user_create_secret_feedback_success() {
        // given
        final CreateFeedbackRequest request = new CreateFeedbackRequest("비밀 피드백입니다", true, "테스트유저", "건의", "https://example.com/image.png");

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(201))
                .body("message", equalTo("CREATED"))
                .body("data.feedbackId", notNullValue())
                .body("data.content", equalTo("비밀 피드백입니다"))
                .body("data.isSecret", equalTo(true))
                .body("data.postedAt", notNullValue())
                .body("data.category", equalTo("건의"));
    }

    @Test
    @DisplayName("사용자가 새로 생성한 피드백이 목록에 나타난다")
    void user_create_feedback_appears_in_list() {
        // given
        final CreateFeedbackRequest request = new CreateFeedbackRequest(
                "새 피드백", false, "테스트유저", "건의", "https://example.com/image.png");

        // when - 피드백 생성
        final Long createdFeedbackId = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .getLong("data.feedbackId");

        // then - 목록에서 확인
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(greaterThan(0)))
                .body("data.feedbacks[0].feedbackId", equalTo(createdFeedbackId.intValue()))
                .body("data.feedbacks[0].content", equalTo("새 피드백"))
                .body("data.feedbacks[0].isSecret", equalTo(false))
                .body("data.feedbacks[0].category", equalTo("건의"));
        ;
    }

    @Test
    @DisplayName("사용자가 특정 장소의 통계를 성공적으로 조회한다")
    void user_get_statistic_success() {
        // given
        // 확인된 피드백 2개
        final Feedback confirmedFeedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        confirmedFeedback1.updateStatus(CONFIRMED);
        confirmedFeedback1.increaseLikeCount();
        confirmedFeedback1.increaseLikeCount();
        confirmedFeedback1.increaseLikeCount();
        confirmedFeedback1.increaseLikeCount();
        confirmedFeedback1.increaseLikeCount();

        final Feedback confirmedFeedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        confirmedFeedback2.updateStatus(CONFIRMED);
        confirmedFeedback1.increaseLikeCount();
        confirmedFeedback1.increaseLikeCount();
        confirmedFeedback1.increaseLikeCount();

        // 대기 중인 피드백 3개
        final Feedback waitingFeedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        waitingFeedback1.updateStatus(WAITING);

        final Feedback waitingFeedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        waitingFeedback2.updateStatus(WAITING);

        final Feedback waitingFeedback3 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        waitingFeedback3.updateStatus(WAITING);

        // 다른 장소의 피드백 (통계에 포함되지 않음)
        final Organization otherOrganization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(otherOrganization);
        final OrganizationCategory otherOrganizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                otherOrganization, SUGGESTION);
        organizationCategoryRepository.save(otherOrganizationCategory);
        final Feedback otherPlaceFeedback = FeedbackFixture.createFeedbackWithOrganization(otherOrganization,
                otherOrganizationCategory);

        // 피드백 저장
        feedbackRepository.save(confirmedFeedback1);
        feedbackRepository.save(confirmedFeedback2);
        feedbackRepository.save(waitingFeedback1);
        feedbackRepository.save(waitingFeedback2);
        feedbackRepository.save(waitingFeedback3);
        feedbackRepository.save(otherPlaceFeedback);

        // when & then
        given()
                .log().all()
                .queryParam("period", "WEEK") // 7일 기간으로 통계 요청
                .when()
                .get("/organizations/{organizationUuid}/statistic", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.reflectionRate", equalTo(40))
                .body("data.confirmedCount", equalTo(2))
                .body("data.waitingCount", equalTo(3))
                .body("data.totalCount", equalTo(5));
    }

    @Test
    @DisplayName("피드백 목록을 최신순으로 반환된다")
    void get_feedbacks_ordered_by_latest() {
        // given
        // 순서대로 저장하여 ID가 증가하도록 함
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);

        final Feedback saved1 = feedbackRepository.save(feedback1);
        final Feedback saved2 = feedbackRepository.save(feedback2);
        final Feedback saved3 = feedbackRepository.save(feedback3);

        // when & then - LATEST 정렬로 조회하면 최신순(ID 역순)으로 반환
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(3))
                .body("data.feedbacks[0].feedbackId", equalTo(saved3.getId().intValue()))
                .body("data.feedbacks[1].feedbackId", equalTo(saved2.getId().intValue()))
                .body("data.feedbacks[2].feedbackId", equalTo(saved1.getId().intValue()));
    }

    @Test
    @DisplayName("피드백 목록을 오래된순으로 반환된다")
    void get_feedbacks_ordered_by_oldest() {
        // given
        // 순서대로 저장하여 ID가 증가하도록 함
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);

        final Feedback saved1 = feedbackRepository.save(feedback1);
        final Feedback saved2 = feedbackRepository.save(feedback2);
        final Feedback saved3 = feedbackRepository.save(feedback3);

        // when & then - OLDEST 정렬로 조회하면 오래된순(ID 순)으로 반환
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("sortBy", "OLDEST")
                .when()
                .get("/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(3))
                .body("data.feedbacks[0].feedbackId", equalTo(saved1.getId().intValue()))
                .body("data.feedbacks[1].feedbackId", equalTo(saved2.getId().intValue()))
                .body("data.feedbacks[2].feedbackId", equalTo(saved3.getId().intValue()));
    }

    @Test
    @DisplayName("피드백 목록을 좋아요 많은 순으로 반환된다")
    void get_feedbacks_ordered_by_likes() {
        // given
        // 좋아요 수가 다른 피드백들 생성
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory,
                5);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory,
                10);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory,
                3);

        final Feedback saved1 = feedbackRepository.save(feedback1);
        final Feedback saved2 = feedbackRepository.save(feedback2);
        final Feedback saved3 = feedbackRepository.save(feedback3);

        // when & then - LIKES 정렬로 조회하면 좋아요 많은순으로 반환 (10, 5, 3)
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("sortBy", "LIKES")
                .when()
                .get("organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(3))
                .body("data.feedbacks[0].feedbackId", equalTo(saved2.getId().intValue()))
                .body("data.feedbacks[0].likeCount", equalTo(10))
                .body("data.feedbacks[1].feedbackId", equalTo(saved1.getId().intValue()))
                .body("data.feedbacks[1].likeCount", equalTo(5))
                .body("data.feedbacks[2].feedbackId", equalTo(saved3.getId().intValue()))
                .body("data.feedbacks[2].likeCount", equalTo(3));
    }

}
