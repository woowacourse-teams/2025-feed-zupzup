package feedzupzup.backend.s3.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.s3.config.LocalStackS3Test;
import feedzupzup.backend.s3.config.S3Properties;
import feedzupzup.backend.s3.dto.request.PresignedUrlRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@LocalStackS3Test
class S3ControllerE2eTest extends E2EHelper {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Properties s3Properties;

    @Test
    @DisplayName("유효한 요청으로 presigned URL을 성공적으로 발급받는다")
    void generate_presigned_url_success() {
        // given
        createBucketIfNotExists();

        final PresignedUrlRequest request = new PresignedUrlRequest("feedbacks", "jpg");

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/presigned-url")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.presignedUrl", notNullValue())
                .body("data.presignedUrl", containsString(s3Properties.bucketName()))
                .body("data.presignedUrl", containsString("feedbacks"))
                .body("data.presignedUrl", containsString(".jpg"))
                .body("data.contentType", equalTo("image/jpeg"));
    }

    @Test
    @DisplayName("PNG 확장자로 presigned URL을 발급받는다")
    void generate_presigned_url_with_png_extension() {
        // given
        createBucketIfNotExists();

        final PresignedUrlRequest request = new PresignedUrlRequest("profiles", "png");

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/presigned-url")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.presignedUrl", notNullValue())
                .body("data.presignedUrl", containsString("profiles"))
                .body("data.presignedUrl", containsString(".png"))
                .body("data.contentType", equalTo("image/png"));
    }

    @Test
    @DisplayName("JPEG 확장자로 presigned URL을 발급받는다")
    void generate_presigned_url_with_jpeg_extension() {
        // given
        createBucketIfNotExists();

        final PresignedUrlRequest request = new PresignedUrlRequest("images", "jpeg");

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/presigned-url")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.presignedUrl", notNullValue())
                .body("data.presignedUrl", containsString("images"))
                .body("data.presignedUrl", containsString(".jpeg"))
                .body("data.contentType", equalTo("image/jpeg"));
    }

    @Test
    @DisplayName("다른 objectDir로 presigned URL을 발급받는다")
    void generate_presigned_url_with_different_object_dir() {
        // given
        createBucketIfNotExists();

        final PresignedUrlRequest request = new PresignedUrlRequest("attachments", "jpg");

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/presigned-url")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.presignedUrl", notNullValue())
                .body("data.presignedUrl", containsString("attachments"))
                .body("data.contentType", equalTo("image/jpeg"));
    }

    @Test
    @DisplayName("objectDir가 null일 경우 500 에러를 반환한다")
    void generate_presigned_url_with_null_object_dir() {
        // given
        createBucketIfNotExists();

        final PresignedUrlRequest request = new PresignedUrlRequest(null, "jpg");

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/presigned-url")
                .then().log().all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(500));
    }

    @Test
    @DisplayName("extension이 null일 경우 500 에러를 반환한다")
    void generate_presigned_url_with_null_extension() {
        // given
        createBucketIfNotExists();

        final PresignedUrlRequest request = new PresignedUrlRequest("feedbacks", null);

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/presigned-url")
                .then().log().all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(500));
    }

    @Test
    @DisplayName("presigned URL의 object key에 UUID가 포함되어 있다")
    void presigned_url_contains_uuid() {
        // given
        createBucketIfNotExists();

        final PresignedUrlRequest request = new PresignedUrlRequest("feedbacks", "jpg");

        // UUID 패턴 (8-4-4-4-12 형식)
        final String uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/presigned-url")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("data.presignedUrl", matchesPattern(".*" + uuidPattern + ".*"));
    }

    @Test
    @DisplayName("동일한 요청으로 여러 번 호출하면 서로 다른 presigned URL을 반환한다")
    void generate_different_presigned_urls_for_same_request() {
        // given
        createBucketIfNotExists();

        final PresignedUrlRequest request = new PresignedUrlRequest("feedbacks", "jpg");

        // when - 첫 번째 요청
        final String firstPresignedUrl = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/presigned-url")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getString("data.presignedUrl");

        // when - 두 번째 요청
        final String secondPresignedUrl = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/presigned-url")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getString("data.presignedUrl");

        // then - 두 URL이 서로 다름
        assert !firstPresignedUrl.equals(secondPresignedUrl);
    }

    private void createBucketIfNotExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .build());
        }
    }
}
