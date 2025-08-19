package feedzupzup.backend.feedback.controller;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.google.cloud.storage.Acl.Role;
import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.application.PasswordEncoder;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AdminFeedbackControllerE2ETest extends E2EHelper {

    private static final String SESSION_ID = "JSESSIONID";

    @Autowired
    private FeedbackRepository feedBackRepository;

    @Autowired
    private FeedbackLikeRepository feedbackLikeRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String sessionCookie;

    private Admin admin;

    @BeforeEach
    void setUp() {
        feedbackLikeRepository.clear();

        final Password password = new Password("password123");
        admin = new Admin(new LoginId("testId"), passwordEncoder.encode(password),
                new AdminName("testName"));
        adminRepository.save(admin);

        final LoginRequest loginRequest = new LoginRequest("testId", "password123");
        sessionCookie = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/admin/login")
                .then()
                .extract()
                .cookie(SESSION_ID);
    }

    @Test
    @DisplayName("관리자가 피드백을 성공적으로 삭제한다")
    void admin_delete_feedback_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithContent(
                organization,
                "삭제될 피드백",
                organizationCategory
        );
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .delete("/admin/feedbacks/{feedbackId}", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"));
    }

    @Test
    @DisplayName("관리자가 피드백 목록을 성공적으로 조회한다")
    void admin_get_feedbacks_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent(
                organization,
                "첫 번째 피드백",
                organizationCategory
        );
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent(
                organization,
                "두 번째 피드백",
                organizationCategory
        );
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent(
                organization,
                "세 번째 피드백",
                organizationCategory);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(3))
                .body("data.hasNext", equalTo(false))
                .body("data.nextCursorId", equalTo(1));
    }

    @Test
    @DisplayName("관리자가 답글을 달 수 있어야 한다.")
    void admin_comment() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithContent(
                organization,
                "첫 번째 피드백",
                organizationCategory
        );
        feedBackRepository.save(feedback);

        UpdateFeedbackCommentRequest updateFeedbackCommentRequest = new UpdateFeedbackCommentRequest(
                "testRequest"
        );

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .contentType(ContentType.JSON)
                .body(updateFeedbackCommentRequest)
                .when()
                .patch("/admin/feedbacks/{feedbackId}/comment", 1)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.comment", equalTo("testRequest"))
                .body("data.feedbackId", equalTo(1));
    }

    @Test
    @DisplayName("관리자가 커서 기반 페이징으로 피드백 목록을 조회한다")
    void admin_get_feedbacks_with_cursor_pagination() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent(
                organization,
                "첫 번째 피드백",
                organizationCategory
        );
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent(
                organization,
                "두 번째 피드백",
                organizationCategory
        );
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent(
                organization,
                "세 번째 피드백",
                organizationCategory
        );

        // 피드백 3개 생성
        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);

        // when - 첫 번째 페이지 조회
        final Long firstPageCursor = given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 2)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(2))
                .body("data.hasNext", equalTo(true))
                .extract()
                .jsonPath()
                .getLong("data.nextCursorId");

        // when - 두 번째 페이지 조회
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 2)
                .queryParam("cursorId", firstPageCursor)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(1))
                .body("data.hasNext", equalTo(false));
    }

    @Test
    @DisplayName("관리자가 빈 피드백 목록을 조회한다")
    void admin_get_empty_feedbacks() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(0))
                .body("data.hasNext", equalTo(false))
                .body("data.nextCursorId", equalTo(null));
    }

    @Test
    @DisplayName("관리자 피드백 목록에서 DB 좋아요 수와 인메모리 좋아요 수가 합산되어 반영된다")
    void admin_get_feedbacks_reflects_memory_likes() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(
                organization,
                organizationCategory,
                5
        ); // DB에 5개 좋아요
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(
                organization,
                organizationCategory,
                3
        ); // DB에 3개 좋아요
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(
                organization,
                organizationCategory,
                0
        ); // DB에 0개 좋아요

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);

        // 인메모리에 좋아요 추가
        feedbackLikeRepository.increaseAndGet(saved1.getId()); // 인메모리에 1개 추가 -> 총 6개
        feedbackLikeRepository.increaseAndGet(saved1.getId()); // 인메모리에 1개 더 추가 -> 총 7개
        feedbackLikeRepository.increaseAndGet(saved2.getId()); // 인메모리에 1개 추가 -> 총 4개
        feedbackLikeRepository.increaseAndGet(saved3.getId()); // 인메모리에 1개 추가 -> 총 1개
        feedbackLikeRepository.increaseAndGet(saved3.getId()); // 인메모리에 1개 더 추가 -> 총 2개
        feedbackLikeRepository.increaseAndGet(saved3.getId()); // 인메모리에 1개 더 추가 -> 총 3개

        // when & then - 좋아요 수가 DB + 인메모리 합산 값으로 반영되는지 확인
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(3))
                .body("data.feedbacks[0].likeCount", equalTo(3)) // saved3: 0(DB) + 3(인메모리) = 3 (최신순)
                .body("data.feedbacks[1].likeCount", equalTo(4)) // saved2: 3(DB) + 1(인메모리) = 4
                .body("data.feedbacks[2].likeCount", equalTo(7)); // saved1: 5(DB) + 2(인메모리) = 7
    }

    @Test
    @DisplayName("관리자 피드백 목록에서 인메모리 좋아요가 없는 피드백은 DB 좋아요 수만 반영된다")
    void admin_get_feedbacks_reflects_only_db_likes_when_no_memory_likes() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(
                organization,
                organizationCategory,
                10
        ); // DB에 10개 좋아요
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(
                organization,
                organizationCategory,
                0
        );  // DB에 0개 좋아요

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);

        // when & then - 인메모리 좋아요 추가 없이 조회, DB 좋아요 수만 반영되는지 확인
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(2))
                .body("data.feedbacks[0].likeCount", equalTo(0))  // DB 좋아요 수만 (최신순)
                .body("data.feedbacks[1].likeCount", equalTo(10)); // DB 좋아요 수만
    }

    @Test
    @DisplayName("관리자 피드백 목록에서 인메모리에만 좋아요가 있는 피드백도 정상적으로 반영된다")
    void admin_get_feedbacks_reflects_only_memory_likes() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(
                organization,
                organizationCategory,
                0
        ); // DB에 0개 좋아요
        final Feedback saved = feedBackRepository.save(feedback);

        // 인메모리에만 좋아요 추가
        for (int i = 0; i < 5; i++) {
            feedbackLikeRepository.increaseAndGet(saved.getId()); // 인메모리에 총 5개 추가
        }

        // when & then - 인메모리 좋아요 수만 반영되는지 확인
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(1))
                .body("data.feedbacks[0].likeCount", equalTo(5)); // 인메모리 좋아요 수만
    }

    @Test
    @DisplayName("관리자 피드백 목록을 최신순으로 반환된다")
    void admin_get_feedbacks_ordered_by_latest() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        // 순서대로 저장하여 ID가 증가하도록 함
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent(organization, "첫 번째 피드백",
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent(organization, "두 번째 피드백",
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent(organization, "세 번째 피드백",
                organizationCategory);

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);

        // when & then - LATEST 정렬로 조회하면 최신순(ID 역순)으로 반환
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 10)
                .queryParam("sortBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(3))
                .body("data.feedbacks[0].feedbackId", equalTo(saved3.getId().intValue()))
                .body("data.feedbacks[1].feedbackId", equalTo(saved2.getId().intValue()))
                .body("data.feedbacks[2].feedbackId", equalTo(saved1.getId().intValue()));
    }

    @Test
    @DisplayName("관리자 피드백 목록을 오래된순으로 반환된다")
    void admin_get_feedbacks_ordered_by_oldest() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        // 순서대로 저장하여 ID가 증가하도록 함
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent(organization, "첫 번째 피드백",
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent(organization, "두 번째 피드백",
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent(organization, "세 번째 피드백",
                organizationCategory);

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);

        // when & then - OLDEST 정렬로 조회하면 오래된순(ID 순)으로 반환
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 10)
                .queryParam("sortBy", "OLDEST")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(3))
                .body("data.feedbacks[0].feedbackId", equalTo(saved1.getId().intValue()))
                .body("data.feedbacks[1].feedbackId", equalTo(saved2.getId().intValue()))
                .body("data.feedbacks[2].feedbackId", equalTo(saved3.getId().intValue()));
    }

    @Test
    @DisplayName("관리자 피드백 목록을 좋아요 많은 순으로 반환된다")
    void admin_get_feedbacks_ordered_by_likes() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        // 좋아요 수가 다른 피드백들 생성
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory,
                5);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory,
                10);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory,
                3);

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);

        // when & then - LIKES 정렬로 조회하면 좋아요 많은순으로 반환 (10, 5, 3)
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("size", 10)
                .queryParam("sortBy", "LIKES")
                .when()
                .get("/admin/organizations/{organizationUuid}/feedbacks", organization.getUuid())
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
