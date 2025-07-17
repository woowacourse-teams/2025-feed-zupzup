package feedzupzup.backend.feedback.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import feedzupzup.backend.feedback.fixture.FeedbackRequestFixture;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class AdminFeedbackControllerE2ETest extends E2EHelper {

    @Test
    @DisplayName("관리자가 피드백을 성공적으로 삭제한다")
    void admin_delete_feedback_success() {
        // given
        final Long placeId = 1L;
        final CreateFeedbackRequest createRequest = FeedbackRequestFixture.createRequestWithContent("삭제될 피드백");

        // 피드백 생성하여 ID 추출
        final Long feedbackId = given()
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/places/{placeId}/feedbacks", placeId)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .getLong("data.feedbackId");

        // when & then
        given()
                .log().all()
                .when()
                .delete("/admin/feedbacks/{feedbackId}", feedbackId)
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
        final Long placeId = 1L;
        final CreateFeedbackRequest createRequest = FeedbackRequestFixture.createRequestWithContent("상태 변경될 피드백");

        // 피드백 생성하여 ID 추출
        final Long feedbackId = given()
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/places/{placeId}/feedbacks", placeId)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .getLong("data.feedbackId");

        final UpdateFeedbackStatusRequest updateRequest = new UpdateFeedbackStatusRequest(ProcessStatus.CONFIRMED);

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .patch("/admin/feedbacks/{feedbackId}/status", feedbackId)
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
        final Long placeId = 1L;
        final CreateFeedbackRequest createRequest = FeedbackRequestFixture.createRequestWithSecret(false);

        // 피드백 생성하여 ID 추출
        final Long feedbackId = given()
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/places/{placeId}/feedbacks", placeId)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .getLong("data.feedbackId");

        final UpdateFeedbackSecretRequest updateRequest = new UpdateFeedbackSecretRequest(true);

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .patch("/admin/feedbacks/{feedbackId}/secret", feedbackId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.feedbackId", equalTo(feedbackId.intValue()))
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
}
