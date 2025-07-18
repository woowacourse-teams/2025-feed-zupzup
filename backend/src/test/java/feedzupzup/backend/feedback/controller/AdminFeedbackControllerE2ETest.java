package feedzupzup.backend.feedback.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AdminFeedbackControllerE2ETest extends E2EHelper {

    @Autowired
    private FeedBackRepository feedBackRepository;

    @Test
    @DisplayName("관리자가 피드백을 성공적으로 삭제한다")
    void admin_delete_feedback_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithContent("삭제될 피드백");
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
        final Feedback feedback = FeedbackFixture.createFeedbackWithContent("상태 변경될 피드백");
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
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("관리자가 피드백 비밀상태를 성공적으로 변경한다")
    void admin_update_feedback_secret_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithSecret(false);
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
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("관리자가 피드백 목록을 성공적으로 조회한다")
    void admin_get_feedbacks_success() {
        // given
        final Long placeId = 1L;
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent("첫 번째 피드백");
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent("두 번째 피드백");
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent("세 번째 피드백");

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .when()
                .get("/admin/places/{placeId}/feedbacks", placeId)
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
    @DisplayName("관리자가 커서 기반 페이징으로 피드백 목록을 조회한다")
    void admin_get_feedbacks_with_cursor_pagination() {
        // given
        final Long placeId = 1L;
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent("첫 번째 피드백");
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent("두 번째 피드백");
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithContent("세 번째 피드백");

        // 피드백 3개 생성
        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);

        // when - 첫 번째 페이지 조회
        final Long firstPageCursor = given()
                .log().all()
                .queryParam("size", 2)
                .when()
                .get("/admin/places/{placeId}/feedbacks", placeId)
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
                .when()
                .get("/admin/places/{placeId}/feedbacks", placeId)
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
        final Long placeId = 999L;

        // when & then
        given()
                .log().all()
                .queryParam("size", 10)
                .when()
                .get("/admin/places/{placeId}/feedbacks", placeId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbacks.size()", equalTo(0))
                .body("data.hasNext", equalTo(false))
                .body("data.nextCursorId", equalTo(null));
    }
}
