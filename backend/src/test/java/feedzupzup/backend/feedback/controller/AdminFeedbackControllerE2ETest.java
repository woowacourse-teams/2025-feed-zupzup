package feedzupzup.backend.feedback.controller;

import static feedzupzup.backend.category.domain.Category.FACILITY;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.CategoryFixture;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AdminFeedbackControllerE2ETest extends E2EHelper {

    @Autowired
    private FeedbackRepository feedBackRepository;

    @Autowired
    private FeedbackLikeRepository feedbackLikeRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @BeforeEach
    void clearMemory() {
        feedbackLikeRepository.clear();
    }

    @Test
    @DisplayName("관리자가 피드백을 성공적으로 삭제한다")
    void admin_delete_feedback_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithContent("삭제될 피드백",
                organizationCategory);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then
        given()
                .log().all()
                .when()
                .delete("/admin/feedbacks/{feedbackId}", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"));
    }

    @Test
    @DisplayName("관리자가 피드백 상태를 성공적으로 업데이트한다")
    void admin_update_feedback_status_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithContent("상태 변경될 피드백",
                organizationCategory);
        final Feedback savedFeedback = feedBackRepository.save(feedback);
        final UpdateFeedbackStatusRequest updateRequest = new UpdateFeedbackStatusRequest(ProcessStatus.CONFIRMED);

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .patch("/admin/feedbacks/{feedbackId}/status", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.status", equalTo("CONFIRMED"));
    }

    @Test
    @DisplayName("존재하지 않는 피드백 상태 업데이트 시 예외가 발생한다")
    void admin_update_feedback_status_not_found() {
        // given
        final Long nonExistentFeedbackId = 999L;
        final UpdateFeedbackStatusRequest updateRequest = new UpdateFeedbackStatusRequest(ProcessStatus.CONFIRMED);

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .patch("/admin/feedbacks/{feedbackId}/status", nonExistentFeedbackId)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("관리자가 피드백 비밀상태를 성공적으로 변경한다")
    void admin_update_feedback_secret_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithSecret(false,
                organizationCategory);
        final Feedback savedFeedback = feedBackRepository.save(feedback);
        final UpdateFeedbackSecretRequest updateRequest = new UpdateFeedbackSecretRequest(true);

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .patch("/admin/feedbacks/{feedbackId}/secret", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbackId", equalTo(savedFeedback.getId().intValue()))
                .body("data.isSecret", equalTo(true));
    }

    @Test
    @DisplayName("존재하지 않는 피드백 비밀상태 변경 시 예외가 발생한다")
    void admin_update_feedback_secret_not_found() {
        // given
        final Long nonExistentFeedbackId = 999L;
        final UpdateFeedbackSecretRequest updateRequest = new UpdateFeedbackSecretRequest(true);

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .patch("/admin/feedbacks/{feedbackId}/secret", nonExistentFeedbackId)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("관리자가 피드백 목록을 성공적으로 조회한다")
    void admin_get_feedbacks_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent("첫 번째 피드백",
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent("두 번째 피드백",
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent("세 번째 피드백",
                organizationCategory);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("orderBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organization.getId())
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

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithContent("첫 번째 피드백",
                organizationCategory);
        feedBackRepository.save(feedback);

        UpdateFeedbackCommentRequest updateFeedbackCommentRequest = new UpdateFeedbackCommentRequest(
                "testRequest"
        );

        // when & then
        given()
                .log().all()
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

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent("첫 번째 피드백",
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent("두 번째 피드백",
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent("세 번째 피드백",
                organizationCategory);

        // 피드백 3개 생성
        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);

        // when - 첫 번째 페이지 조회
        final Long firstPageCursor = given()
                .log().all()
                .queryParam("size", 2)
                .queryParam("orderBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organization.getId())
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
                .queryParam("size", 2)
                .queryParam("cursorId", firstPageCursor)
                .queryParam("orderBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organization.getId())
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
        final Long organizationId = 999L;

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("orderBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organizationId)
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

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization.getId(),
                organizationCategory, 5); // DB에 5개 좋아요
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization.getId(),
                organizationCategory, 3); // DB에 3개 좋아요
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(organization.getId(),
                organizationCategory, 0); // DB에 0개 좋아요

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
                .queryParam("size", 10)
                .queryParam("orderBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organization.getId())
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

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization.getId(),
                organizationCategory, 10); // DB에 10개 좋아요
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization.getId(),
                organizationCategory, 0);  // DB에 0개 좋아요

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);

        // when & then - 인메모리 좋아요 추가 없이 조회, DB 좋아요 수만 반영되는지 확인
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("orderBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organization.getId())
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

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization.getId(),
                organizationCategory, 0); // DB에 0개 좋아요
        final Feedback saved = feedBackRepository.save(feedback);

        // 인메모리에만 좋아요 추가
        for (int i = 0; i < 5; i++) {
            feedbackLikeRepository.increaseAndGet(saved.getId()); // 인메모리에 총 5개 추가
        }

        // when & then - 인메모리 좋아요 수만 반영되는지 확인
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("orderBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organization.getId())
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

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        // 순서대로 저장하여 ID가 증가하도록 함
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent("첫 번째 피드백", organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent("두 번째 피드백", organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent("세 번째 피드백", organizationCategory);

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);

        // when & then - LATEST 정렬로 조회하면 최신순(ID 역순)으로 반환
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("orderBy", "LATEST")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organization.getId())
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

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        // 순서대로 저장하여 ID가 증가하도록 함
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent("첫 번째 피드백", organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent("두 번째 피드백", organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent("세 번째 피드백", organizationCategory);

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);

        // when & then - OLDEST 정렬로 조회하면 오래된순(ID 순)으로 반환
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("orderBy", "OLDEST")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organization.getId())
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

        final OrganizationCategory organizationCategory = CategoryFixture.createOrganizationCategory(
                organization, FACILITY);
        organizationCategoryRepository.save(organizationCategory);

        // 좋아요 수가 다른 피드백들 생성
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization.getId(), organizationCategory,
                5);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization.getId(), organizationCategory,
                10);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(organization.getId(), organizationCategory,
                3);

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);

        // when & then - LIKES 정렬로 조회하면 좋아요 많은순으로 반환 (10, 5, 3)
        given()
                .log().all()
                .queryParam("size", 10)
                .queryParam("orderBy", "LIKES")
                .when()
                .get("/admin/organizations/{organizationId}/feedbacks", organization.getId())
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
