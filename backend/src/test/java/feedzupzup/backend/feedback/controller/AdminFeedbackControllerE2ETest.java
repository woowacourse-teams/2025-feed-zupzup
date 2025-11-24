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
import feedzupzup.backend.feedback.domain.EmbeddingCluster;
import feedzupzup.backend.feedback.domain.EmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.domain.OrganizationStatistic;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
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

    private static final String SESSION_ID = "SESSION";

    @Autowired
    private FeedbackRepository feedBackRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private OrganizationStatisticRepository organizationStatisticRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmbeddingClusterRepository embeddingClusterRepository;

    @Autowired
    private FeedbackEmbeddingClusterRepository feedbackEmbeddingClusterRepository;

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

        organizationStatisticRepository.save(new OrganizationStatistic(organization));

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

        organizationStatisticRepository.save(new OrganizationStatistic(organization));

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

        organizationStatisticRepository.save(new OrganizationStatistic(organization));

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

        organizationStatisticRepository.save(new OrganizationStatistic(organization));

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

        organizationStatisticRepository.save(new OrganizationStatistic(organization));

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

        organizationStatisticRepository.save(new OrganizationStatistic(organization));

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
        organizationStatisticRepository.save(new OrganizationStatistic(organization));

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
        organizationStatisticRepository.save(new OrganizationStatistic(organization));


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
    void admin_get_all_cluster_representatives_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationStatisticRepository.save(new OrganizationStatistic(organization));

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        // 첫 번째 클러스터 생성 (피드백 3개)
        final EmbeddingCluster cluster1 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
        cluster1.updateLabel("첫 번째 클러스터");
        embeddingClusterRepository.save(cluster1);

        // 두 번째 클러스터 생성 (피드백 2개)  
        final EmbeddingCluster cluster2 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
        cluster2.updateLabel("두 번째 클러스터");
        embeddingClusterRepository.save(cluster2);

        // 첫 번째 클러스터에 피드백 3개 추가
        createFeedbackWithCluster(organization, "첫 번째 클러스터 피드백1", organizationCategory, cluster1);
        createFeedbackWithCluster(organization, "첫 번째 클러스터 피드백2", organizationCategory, cluster1);
        createFeedbackWithCluster(organization, "첫 번째 클러스터 피드백3", organizationCategory, cluster1);

        // 두 번째 클러스터에 피드백 2개 추가
        createFeedbackWithCluster(organization, "두 번째 클러스터 피드백1", organizationCategory, cluster2);
        createFeedbackWithCluster(organization, "두 번째 클러스터 피드백2", organizationCategory, cluster2);

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .queryParam("limit", 10)
                .when()
                .get("/admin/organizations/{organizationUuid}/clusters", organization.getUuid())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.clusterInfos", hasSize(2))
                .body("data.clusterInfos[0].totalCount", equalTo(3))
                .body("data.clusterInfos[0].clusterId", equalTo(cluster1.getId().intValue()))
                .body("data.clusterInfos[0].label", equalTo("첫 번째 클러스터"))
                .body("data.clusterInfos[1].totalCount", equalTo(2))
                .body("data.clusterInfos[1].clusterId", equalTo(cluster2.getId().intValue()))
                .body("data.clusterInfos[1].label", equalTo("두 번째 클러스터"));
    }

    @Test
    @DisplayName("관리자가 특정 클러스터의 모든 피드백을 성공적으로 조회한다")
    void admin_get_cluster_feedbacks_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationStatisticRepository.save(new OrganizationStatistic(organization));

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        // 클러스터 생성
        final EmbeddingCluster targetCluster = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
        targetCluster.updateLabel("테스트 클러스터");
        embeddingClusterRepository.save(targetCluster);

        // 클러스터에 피드백 3개 추가
        createFeedbackWithCluster(organization, "첫 번째 피드백", organizationCategory, targetCluster);
        createFeedbackWithCluster(organization, "두 번째 피드백", organizationCategory, targetCluster);
        createFeedbackWithCluster(organization, "세 번째 피드백", organizationCategory, targetCluster);

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations/{organizationUuid}/clusters/{clusterId}", organization.getUuid(), targetCluster.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks", hasSize(3))
                .body("data.label", equalTo("테스트 클러스터"))
                .body("data.totalCount", equalTo(3))
                .body("data.feedbacks[0].content", equalTo("첫 번째 피드백"))
                .body("data.feedbacks[1].content", equalTo("두 번째 피드백"))
                .body("data.feedbacks[2].content", equalTo("세 번째 피드백"));
    }

    @Test
    @DisplayName("관리자가 존재하지 않는 클러스터를 조회하면 404 예외를 발생시킨다.")
    void admin_get_cluster_feedbacks_not_found() {
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationStatisticRepository.save(new OrganizationStatistic(organization));

        Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);
        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations/{organizationUuid}/clusters/{clusterId}", organization.getUuid(), 99999L)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private void createFeedbackWithCluster(Organization organization, String content,
                                           OrganizationCategory category, EmbeddingCluster cluster) {
        final Feedback feedback = FeedbackFixture.createFeedbackWithContent(organization, content, category);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        final FeedbackEmbeddingCluster feedbackCluster = FeedbackEmbeddingCluster.createNewCluster(
                new double[]{0.1, 0.2, 0.3}, savedFeedback, cluster);
        feedbackEmbeddingClusterRepository.save(feedbackCluster);
    }

    @Test
    @DisplayName("비동기 다운로드 작업 생성 API 호출 성공")
    void create_download_job_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationStatisticRepository.save(new OrganizationStatistic(organization));
        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .post("/admin/organizations/{organizationUuid}/download-jobs", organization.getUuid())
                .then()
                .log().all()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body("data.jobId", org.hamcrest.Matchers.notNullValue())
                .body("status", equalTo(202));
    }

    @Test
    @DisplayName("존재하지 않는 단체로 다운로드 작업 생성 시 403 에러 (권한 없음)")
    void create_download_job_organization_not_found() {
        // given
        final UUID nonExistentUuid = UUID.randomUUID();

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .post("/admin/organizations/{organizationUuid}/download-jobs", nonExistentUuid)
                .then()
                .log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("다운로드 작업 상태 조회 API 호출 성공")
    void get_download_job_status_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationStatisticRepository.save(new OrganizationStatistic(organization));
        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        // 작업 생성
        final String jobId = given()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .post("/admin/organizations/{organizationUuid}/download-jobs", organization.getUuid())
                .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .extract()
                .path("data.jobId");

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations/{organizationUuid}/download-jobs/{jobId}/status",
                        organization.getUuid(), jobId)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.jobStatus", org.hamcrest.Matchers.notNullValue())
                .body("data.progress", org.hamcrest.Matchers.greaterThanOrEqualTo(0))
                .body("status", equalTo(200));
    }

    @Test
    @DisplayName("존재하지 않는 작업 ID로 상태 조회 시 404 에러")
    void get_download_job_status_not_found() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationStatisticRepository.save(new OrganizationStatistic(organization));
        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final String nonExistentJobId = UUID.randomUUID().toString();

        // when & then
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations/{organizationUuid}/download-jobs/{jobId}/status",
                        organization.getUuid(), nonExistentJobId)
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("완료되지 않은 작업의 다운로드 요청 시 400 에러")
    void download_file_not_completed() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationStatisticRepository.save(new OrganizationStatistic(organization));
        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        // 작업 생성
        final String jobId = given()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .post("/admin/organizations/{organizationUuid}/download-jobs", organization.getUuid())
                .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .extract()
                .path("data.jobId");

        // when & then - 파일 생성 완료 전에 다운로드 요청
        given()
                .log().all()
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations/{organizationUuid}/download-jobs/{jobId}",
                        organization.getUuid(), jobId)
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value()); // DownloadJobNotCompletedException으로 400 반환
    }

}
