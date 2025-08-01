package feedzupzup.backend.feedback.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class FeedbackLikeControllerE2ETest extends E2EHelper {

    @Autowired
    private FeedBackRepository feedBackRepository;

    @Autowired
    private FeedbackLikeRepository feedbackLikeRepository;

    @BeforeEach
    void clearMemory() {
        feedbackLikeRepository.clear();
    }

    @Test
    @DisplayName("피드백에 좋아요를 성공적으로 추가한다")
    void like_feedback_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 5);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then
        given()
                .log().all()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.beforeLikeCount", equalTo(0))
                .body("data.afterLikeCount", equalTo(1));
    }

    @Test
    @DisplayName("이미 좋아요가 있는 피드백에 추가 좋아요를 성공적으로 추가한다")
    void like_feedback_with_existing_likes_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 10);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // 먼저 인메모리에 좋아요 2개 추가
        feedbackLikeRepository.increaseAndGet(savedFeedback.getId());
        feedbackLikeRepository.increaseAndGet(savedFeedback.getId());

        // when & then
        given()
                .log().all()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.beforeLikeCount", equalTo(2))
                .body("data.afterLikeCount", equalTo(3));
    }

    @Test
    @DisplayName("존재하지 않는 피드백에 좋아요를 시도하면 404 에러가 발생한다")
    void like_non_existent_feedback_not_found() {
        // given
        final Long nonExistentFeedbackId = 999L;

        // when & then
        given()
                .log().all()
                .when()
                .post("/feedbacks/{feedbackId}/like", nonExistentFeedbackId)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("같은 피드백에 여러 번 좋아요를 추가한다")
    void like_feedback_multiple_times_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 0);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then - 첫 번째 좋아요
        given()
                .log().all()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.beforeLikeCount", equalTo(0))
                .body("data.afterLikeCount", equalTo(1));

        // when & then - 두 번째 좋아요
        given()
                .log().all()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.beforeLikeCount", equalTo(1))
                .body("data.afterLikeCount", equalTo(2));

        // when & then - 세 번째 좋아요
        given()
                .log().all()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.beforeLikeCount", equalTo(2))
                .body("data.afterLikeCount", equalTo(3));
    }

    @Test
    @DisplayName("피드백 좋아요를 성공적으로 취소한다")
    void unlike_feedback_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 5);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // 먼저 좋아요 3개 추가
        feedbackLikeRepository.increaseAndGet(savedFeedback.getId());
        feedbackLikeRepository.increaseAndGet(savedFeedback.getId());
        feedbackLikeRepository.increaseAndGet(savedFeedback.getId());

        // when & then
        given()
                .log().all()
                .when()
                .delete("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.beforeLikeCount", equalTo(3))
                .body("data.afterLikeCount", equalTo(2));
    }

    @Test
    @DisplayName("좋아요가 0인 피드백에서 좋아요를 취소하면 음수가 된다")
    void unlike_feedback_with_zero_likes_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 0);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then
        given()
                .log().all()
                .when()
                .delete("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.beforeLikeCount", equalTo(0))
                .body("data.afterLikeCount", equalTo(-1));
    }

    @Test
    @DisplayName("존재하지 않는 피드백에 좋아요 취소를 시도하면 404 에러가 발생한다")
    void unlike_non_existent_feedback_not_found() {
        // given
        final Long nonExistentFeedbackId = 999L;

        // when & then
        given()
                .log().all()
                .when()
                .delete("/feedbacks/{feedbackId}/like", nonExistentFeedbackId)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("좋아요 추가 후 취소하면 원래 상태로 돌아간다")
    void like_then_unlike_feedback_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 2);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when - 좋아요 추가
        given()
                .log().all()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.beforeLikeCount", equalTo(0))
                .body("data.afterLikeCount", equalTo(1));

        // then - 좋아요 취소
        given()
                .log().all()
                .when()
                .delete("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.beforeLikeCount", equalTo(1))
                .body("data.afterLikeCount", equalTo(0));
    }

    @Test
    @DisplayName("여러 번의 좋아요와 취소를 반복해도 정확한 카운트를 유지한다")
    void multiple_like_unlike_operations_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 0);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then - 좋아요 추가
        given()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(1));

        // when & then - 좋아요 추가
        given()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(2));

        // when & then - 좋아요 취소
        given()
                .when()
                .delete("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(1));

        // when & then - 좋아요 추가
        given()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(2));

        // when & then - 좋아요 취소
        given()
                .when()
                .delete("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.afterLikeCount", equalTo(1));
    }

    @Test
    @DisplayName("유효한 피드백 ID에 정상적으로 좋아요가 동작한다")
    void like_valid_feedback_id_success() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 0);
        final Feedback savedFeedback = feedBackRepository.save(feedback);

        // when & then
        given()
                .log().all()
                .when()
                .post("/feedbacks/{feedbackId}/like", savedFeedback.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.beforeLikeCount", equalTo(0))
                .body("data.afterLikeCount", equalTo(1));
    }
}
