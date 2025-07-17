package feedzupzup.backend.feedback.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
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
        final CreateFeedbackRequest createRequest = new CreateFeedbackRequest(
                "삭제될 피드백",
                "https://example.com/image.jpg",
                false
        );

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
}
