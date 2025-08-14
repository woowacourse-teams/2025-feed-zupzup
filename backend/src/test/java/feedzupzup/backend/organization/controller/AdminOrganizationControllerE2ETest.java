package feedzupzup.backend.organization.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.application.PasswordEncoder;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AdminOrganizationControllerE2ETest extends E2EHelper {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("정상적인 조직 생성 요청시 조직 생성이 성공한다")
    void createOrganization_Success() {
        // given
        final Password password = Password.createPassword("password123");
        Admin admin = new Admin(new LoginId("testId"), passwordEncoder.encode(password), new AdminName("testName"));
        adminRepository.save(admin);

        LoginRequest loginRequest = new LoginRequest("testId", "password123");
        String sessionCookie = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/admin/login")
                .then()
                .extract()
                .cookie("JSESSIONID");

        CreateOrganizationRequest request = new CreateOrganizationRequest("우아한테크코스", List.of("신고", "건의"));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .cookie("JSESSIONID", sessionCookie)
                .body(request)
        .when()
                .post("/admin/organizations")
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .body("data.organizationId", notNullValue());
    }

    @Test
    @DisplayName("admin 권한 없이 요청이 올 경우, 권한 오류가 발생해야 한다.")
    void not_authorization_case() {
        // given
        CreateOrganizationRequest request = new CreateOrganizationRequest("우아한테크코스", List.of("신고", "건의"));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/admin/organizations")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}