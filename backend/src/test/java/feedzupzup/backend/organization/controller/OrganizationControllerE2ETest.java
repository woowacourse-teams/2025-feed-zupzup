package feedzupzup.backend.organization.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
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
        final Organization organization = new Organization("우아한테크코스");
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
}
