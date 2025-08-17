package feedzupzup.backend.organization.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.application.PasswordEncoder;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import io.restassured.http.ContentType;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AdminOrganizationControllerE2ETest extends E2EHelper {

    private static final String SESSION_ID = "JSESSIONID";

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("정상적인 조직 생성 요청시 조직 생성이 성공한다")
    void createOrganization_Success() {
        final Password password = new Password("password123");
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
                .cookie(SESSION_ID);

        CreateOrganizationRequest request = new CreateOrganizationRequest("우아한테크코스", Set.of("신고", "건의"));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .cookie(SESSION_ID, sessionCookie)
                .body(request)
                .when()
                .post("/admin/organizations")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .body("data.organizationUuid", notNullValue());
    }

    @Test
    @DisplayName("admin 권한 없이 요청이 올 경우, 권한 오류가 발생해야 한다.")
    void not_authorization_case() {
        // given
        CreateOrganizationRequest request = new CreateOrganizationRequest("우아한테크코스", Set.of("신고", "건의"));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/admin/organizations")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("정상적인 조직 조회 요청시 조직 조회가 성공한다")
    void getOrganizations_Success() {
        // given
        final Password password = new Password("password123");
        Admin admin = new Admin(new LoginId("testId"), passwordEncoder.encode(password), new AdminName("testName"));
        adminRepository.save(admin);

        Organization organization1 = organizationRepository.save(
                OrganizationFixture.createAllBlackBox());
        Organization organization2 = organizationRepository.save(
                OrganizationFixture.createAllBlackBox());
        organizerRepository.save(new Organizer(organization1, admin, OrganizerRole.OWNER));
        organizerRepository.save(new Organizer(organization2, admin, OrganizerRole.OWNER));

        LoginRequest loginRequest = new LoginRequest("testId", "password123");
        String sessionCookie = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/admin/login")
                .then()
                .extract()
                .cookie(SESSION_ID);

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .body("data.size()", equalTo(2));
    }

    @Test
    @DisplayName("소속된 조직이 없는 경우, 빈 리스트를 반환한다.")
    void getOrganizations_Success_Empty() {
        // given
        final Password password = new Password("password123");
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
                .cookie(SESSION_ID);

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .cookie(SESSION_ID, sessionCookie)
                .when()
                .get("/admin/organizations")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .body("data.size()", equalTo(0));
    }

    @Test
    @DisplayName("admin 권한 없이 조직 조회 요청이 올 경우, 권한 오류가 발생해야 한다.")
    void getOrganizations_Fail_Unauthorized() {
        // When & Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/admin/organizations")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("정상적인 조직 수정 요청시 조직 수정이 성공한다")
    void updateOrganization_Success() {
        // given
        final Password password = new Password("password123");
        Admin admin = new Admin(new LoginId("testId"), passwordEncoder.encode(password), new AdminName("testName"));
        adminRepository.save(admin);

        Organization organization = organizationRepository.save(
                OrganizationFixture.createAllBlackBox());
        organizerRepository.save(new Organizer(organization, admin, OrganizerRole.OWNER));

        LoginRequest loginRequest = new LoginRequest("testId", "password123");
        String sessionCookie = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/admin/login")
                .then()
                .extract()
                .cookie(SESSION_ID);

        UpdateOrganizationRequest request = new UpdateOrganizationRequest("새로운 조직", Set.of("신고", "건의", "기타"));

        // when & then
        given()
                .contentType(ContentType.JSON)
                .cookie(SESSION_ID, sessionCookie)
                .body(request)
                .when()
                .put("/admin/organizations/" + organization.getUuid())
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .body("data.updateName", equalTo("새로운 조직"))
                .body("data.updateCategories.size()", equalTo(3));
    }
}
