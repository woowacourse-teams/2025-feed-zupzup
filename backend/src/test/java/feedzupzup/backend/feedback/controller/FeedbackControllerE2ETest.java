package feedzupzup.backend.feedback.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import feedzupzup.backend.config.TestcontainersTest;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.place.domain.Place;
import feedzupzup.backend.place.domain.PlaceRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestcontainersTest
class FeedbackControllerE2ETest extends E2EHelper {

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("피드백을 성공적으로 생성한다")
    void create_secret_feedback_success() {
        // given
        final Place place = new Place("테스트장소", "테스트Url");
        final Place savedPlace = placeRepository.save(place);
        final CreateFeedbackRequest request = new CreateFeedbackRequest(
                "개선이 필요해요",
                true,
                "테스트유저"
        );

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/places/{placeId}/feedbacks", savedPlace.getId())
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(201))
                .body("message", equalTo("CREATED"))
                .body("data.feedbackId", notNullValue())
                .body("data.content", equalTo("개선이 필요해요"))
                .body("data.isSecret", equalTo(true))
                .body("data.createdAt", notNullValue());
    }
}
