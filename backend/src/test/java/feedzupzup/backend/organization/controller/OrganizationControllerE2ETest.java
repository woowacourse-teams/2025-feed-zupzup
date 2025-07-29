package feedzupzup.backend.organization.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class OrganizationControllerE2ETest extends E2EHelper {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("단체 ID로 단체 이름을 성공적으로 조회한다")
    void get_organization_name_success() {
        // given
        final Organization organization = OrganizationFixture.createAllRandom();
        final Organization savedOrganization = organizationRepository.save(organization);

        // when & then
        given()
                .log().all()
                .when()
                .get("/organizations/{organizationId}", savedOrganization.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(200))
                .body("message", equalTo("OK"))
                .body("data.organizationName", equalTo("우아한테크코스"));
    }

    @Test
    @DisplayName("존재하지 않는 단체 ID로 조회 시 404 에러를 반환한다")
    void get_organization_name_not_found() {
        // given
        final Long nonExistentOrganizationId = 999L;

        // when & then
        given()
                .log().all()
                .when()
                .get("/organizations/{organizationId}", nonExistentOrganizationId)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("요청한 응원수만큼 해당 단체 id로 조회된 단체의 총 응원수가 증가한다.")
    void cheer_organization_by_id() {
        // given
        final Organization organization = OrganizationFixture.createAllRandom();

        final Organization savedOrganization = organizationRepository.save(organization);

        CheeringRequest request = new CheeringRequest(100);

        // when & then
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/organizations/{organizationId}/cheering", savedOrganization.getId())
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("cheeringTotalCount", equalTo(100));
    }
}
