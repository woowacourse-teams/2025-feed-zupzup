package feedzupzup.backend.qr.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.qr.domain.QR;
import feedzupzup.backend.qr.repository.QRRepository;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class QRControllerE2ETest extends E2EHelper {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private QRRepository qrRepository;

    @Nested
    @DisplayName("QR 코드 조회 테스트")
    class GetQRTest {

        @Test
        @DisplayName("조직 UUID로 QR 코드를 성공적으로 조회한다")
        void getQR_success() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            final Organization savedOrganization = organizationRepository.save(organization);

            final QR qr = new QR("https://example.com/qr-image.png", savedOrganization);
            qrRepository.save(qr);

            final String expectedSiteUrl = "https://feedzupzup.com/dashboard?uuid=" + savedOrganization.getUuid();

            // when & then
            given()
                    .log().all()
                    .when()
                    .get("/organizations/{organizationUuid}/qr-code", savedOrganization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .contentType(ContentType.JSON)
                    .body("status", equalTo(200))
                    .body("message", equalTo("OK"))
                    .body("data", notNullValue())
                    .body("data.imageUrl", equalTo("https://example.com/qr-image.png"))
                    .body("data.siteUrl", equalTo(expectedSiteUrl));
        }

        @Test
        @DisplayName("존재하지 않는 조직 UUID로 QR 조회 시 404 에러를 반환한다")
        void getQR_organization_not_found() {
            // given
            final UUID nonExistentOrganizationUuid = UUID.randomUUID();

            // when & then
            given()
                    .log().all()
                    .when()
                    .get("/organizations/{organizationUuid}/qr-code", nonExistentOrganizationUuid)
                    .then().log().all()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("조직은 존재하지만 QR이 없을 때 404 에러를 반환한다")
        void getQR_qr_not_found() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            final Organization savedOrganization = organizationRepository.save(organization);

            // when & then
            given()
                    .log().all()
                    .when()
                    .get("/organizations/{organizationUuid}/qr-code", savedOrganization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(ContentType.JSON);
        }
    }
}
