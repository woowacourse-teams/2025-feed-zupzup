package feedzupzup.backend.feedback.controller;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import io.restassured.http.ContentType;
import java.util.UUID;
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

    @Test
    @DisplayName("관리자가 전체 피드백 통계를 성공적으로 조회한다")
    void admin_get_all_feedback_statistics_success() {
        // given
        final Organization organization1 = OrganizationFixture.createAllBlackBox();
        final Organization organization2 = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization1);
        organizationRepository.save(organization2);

        // 관리자를 두 조직에 등록
        final Organizer organizer1 = new Organizer(organization1, admin, OrganizerRole.OWNER);
        final Organizer organizer2 = new Organizer(organization2, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer1);
        organizerRepository.save(organizer2);

        final OrganizationCategory orgCategory1 = OrganizationCategoryFixture.createOrganizationCategory(
                organization1, SUGGESTION);
        final OrganizationCategory orgCategory2 = OrganizationCategoryFixture.createOrganizationCategory(
                organization2, SUGGESTION);
        organizationCategoryRepository.save(orgCategory1);
        organizationCategoryRepository.save(orgCategory2);

        // organization1에 피드백 3개 (완료 2개, 대기 1개)
        final Feedback confirmedFeedback1 = FeedbackFixture.createFeedbackWithStatus(organization1,
                ProcessStatus.CONFIRMED, orgCategory1);
        final Feedback confirmedFeedback2 = FeedbackFixture.createFeedbackWithStatus(organization1,
                ProcessStatus.CONFIRMED, orgCategory1);
        final Feedback waitingFeedback1 = FeedbackFixture.createFeedbackWithStatus(organization1, ProcessStatus.WAITING,
                orgCategory1);

        // organization2에 피드백 2개 (완료 1개, 대기 1개)
        final Feedback confirmedFeedback3 = FeedbackFixture.createFeedbackWithStatus(organization2,
                ProcessStatus.CONFIRMED, orgCategory2);
        final Feedback waitingFeedback2 = FeedbackFixture.createFeedbackWithStatus(organization2, ProcessStatus.WAITING,
                orgCategory2);

        feedBackRepository.save(confirmedFeedback1);
        feedBackRepository.save(confirmedFeedback2);
        feedBackRepository.save(waitingFeedback1);
        feedBackRepository.save(confirmedFeedback3);
        feedBackRepository.save(waitingFeedback2);

        // when & then - 총 5개 피드백 중 3개 완료 (60%)
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/feedbacks/statistics")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.totalCount", equalTo(5))
                .body("data.confirmedCount", equalTo(3))
                .body("data.reflectionRate", equalTo(60));
    }

    @Test
    @DisplayName("관리자에게 피드백이 없는 경우 모든 통계가 0으로 반환된다")
    void admin_get_all_feedback_statistics_no_feedbacks() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/feedbacks/statistics")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.totalCount", equalTo(0))
                .body("data.confirmedCount", equalTo(0))
                .body("data.reflectionRate", equalTo(0));
    }

    @Test
    @DisplayName("관리자 통계에서 삭제된 피드백은 제외된다")
    void admin_get_all_feedback_statistics_excludes_deleted() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory orgCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(orgCategory);

        // 피드백 2개 생성 (완료 1개, 대기 1개)
        final Feedback confirmedFeedback = FeedbackFixture.createFeedbackWithStatus(
                organization, ProcessStatus.CONFIRMED, orgCategory);
        final Feedback waitingFeedback = FeedbackFixture.createFeedbackWithStatus(
                organization, ProcessStatus.WAITING, orgCategory);

        feedBackRepository.save(confirmedFeedback);
        final Feedback savedWaiting = feedBackRepository.save(waitingFeedback);

        // 하나의 피드백 삭제
        feedBackRepository.deleteById(savedWaiting.getId());

        // when & then - 삭제된 피드백은 제외되어 총 1개 (완료 1개, 100%)
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/feedbacks/statistics")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.totalCount", equalTo(1))
                .body("data.confirmedCount", equalTo(1))
                .body("data.reflectionRate", equalTo(100));
    }

    @Test
    @DisplayName("관리자 통계에서 반영률이 올바르게 반올림된다")
    void admin_get_all_feedback_statistics_reflection_rate_rounding() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory orgCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(orgCategory);

        // 총 3개 피드백 중 1개 완료 (33.33...% → 33%)
        final Feedback confirmedFeedback = FeedbackFixture.createFeedbackWithStatus(
                organization, ProcessStatus.CONFIRMED, orgCategory);
        final Feedback waitingFeedback1 = FeedbackFixture.createFeedbackWithStatus(
                organization, ProcessStatus.WAITING, orgCategory);
        final Feedback waitingFeedback2 = FeedbackFixture.createFeedbackWithStatus(
                organization, ProcessStatus.WAITING, orgCategory);

        feedBackRepository.save(confirmedFeedback);
        feedBackRepository.save(waitingFeedback1);
        feedBackRepository.save(waitingFeedback2);

        // when & then - 1/3 = 33.33...% → 33%로 반올림
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/feedbacks/statistics")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.totalCount", equalTo(3))
                .body("data.confirmedCount", equalTo(1))
                .body("data.reflectionRate", equalTo(33));
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 피드백 통계를 조회하면 401 에러가 발생한다")
    void admin_get_all_feedback_statistics_unauthorized() {
        // when & then
        given()
                .log().all()
                .when()
                .get("/admin/feedback-statistics")
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("관리자가 모든 클러스터 대표 피드백을 성공적으로 조회한다")
    void admin_get_representative_cluster_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        // 클러스터별 피드백 생성 (clusterId가 다른 피드백들)
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithCluster(
                organization, "첫 번째 클러스터 피드백", organizationCategory, 
                UUID.fromString("11111111-1111-1111-1111-111111111111"));
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithCluster(
                organization, "첫 번째 클러스터의 다른 피드백", organizationCategory, 
                UUID.fromString("11111111-1111-1111-1111-111111111111"));
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithCluster(
                organization, "두 번째 클러스터 피드백", organizationCategory, 
                UUID.fromString("22222222-2222-2222-2222-222222222222"));

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations/{organizationUuid}/clusters", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.clusterRepresentativeFeedbacks", hasSize(2))
                .body("data.clusterRepresentativeFeedbacks[0].clusterId", equalTo("11111111-1111-1111-1111-111111111111"))
                .body("data.clusterRepresentativeFeedbacks[0].content", equalTo("첫 번째 클러스터 피드백"))
                .body("data.clusterRepresentativeFeedbacks[0].totalCount", equalTo(2))
                .body("data.clusterRepresentativeFeedbacks[1].clusterId", equalTo("22222222-2222-2222-2222-222222222222"))
                .body("data.clusterRepresentativeFeedbacks[1].content", equalTo("두 번째 클러스터 피드백"))
                .body("data.clusterRepresentativeFeedbacks[1].totalCount", equalTo(1));
    }

    @Test
    @DisplayName("관리자가 특정 클러스터의 모든 피드백을 성공적으로 조회한다")
    void admin_get_feedbacks_by_cluster_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final UUID clusterId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        // 같은 클러스터의 피드백 3개 생성
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithCluster(
                organization, "첫 번째 피드백", organizationCategory, clusterId);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithCluster(
                organization, "두 번째 피드백", organizationCategory, clusterId);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithCluster(
                organization, "세 번째 피드백", organizationCategory, clusterId);

        // 다른 클러스터의 피드백 1개 (결과에 포함되지 않아야 함)
        final Feedback otherClusterFeedback = FeedbackFixture.createFeedbackWithCluster(
                organization, "다른 클러스터 피드백", organizationCategory, 
                UUID.fromString("22222222-2222-2222-2222-222222222222"));

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);
        feedBackRepository.save(otherClusterFeedback);

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations/clusters/{clusterId}", clusterId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(3))
                .body("data.feedbacks[0].content", equalTo("첫 번째 피드백"))
                .body("data.feedbacks[1].content", equalTo("두 번째 피드백"))
                .body("data.feedbacks[2].content", equalTo("세 번째 피드백"));
    }

    @Test
    @DisplayName("관리자가 존재하지 않는 클러스터를 조회하면 404 예외를 발생시킨다.")
    void admin_get_feedbacks_by_cluster_empty() {
        // given
        final UUID nonExistentClusterId = UUID.randomUUID();

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations/clusters/{clusterId}", nonExistentClusterId)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(404))
                .body("message", equalTo("요청한 자원을 찾을 수 없습니다"));
    }
}
