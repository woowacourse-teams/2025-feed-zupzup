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
import feedzupzup.backend.organization.domain.OrganizationStatistic;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
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
    private OrganizationStatisticRepository organizationStatisticRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    Organization organization;
    OrganizationCategory organizationCategory;

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationStatisticRepository.save(new OrganizationStatistic(organization));

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

    @Test
    @DisplayName("피드백 조회 시 imageUrl 필드가 포함되어야 한다 - imageUrl이 있는 경우")
    void get_feedbacks_with_imageUrl() {
        // given
        final String imageUrl = "https://example.com/test-image.png";
        final Feedback feedbackWithImage = FeedbackFixture.createFeedbackWithImageUrl(
                organization, organizationCategory, imageUrl);
        final Feedback savedFeedback = feedbackRepository.save(feedbackWithImage);

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
                .body("data.feedbacks", hasSize(1))
                .body("data.feedbacks[0].feedbackId", equalTo(savedFeedback.getId().intValue()))
                .body("data.feedbacks[0].imageUrl", equalTo(imageUrl));
    }

    @Test
    @DisplayName("피드백 조회 시 imageUrl 필드가 포함되어야 한다 - imageUrl이 없는 경우")
    void get_feedbacks_without_imageUrl() {
        // given
        final Feedback feedbackWithoutImage = FeedbackFixture.createFeedbackWithOrganization(
                organization, organizationCategory);
        final Feedback savedFeedback = feedbackRepository.save(feedbackWithoutImage);

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
                .body("data.feedbacks", hasSize(1))
                .body("data.feedbacks[0].feedbackId", equalTo(savedFeedback.getId().intValue()))
                .body("data.feedbacks[0].imageUrl", equalTo(null));
    }

}
