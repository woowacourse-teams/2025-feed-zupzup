package feedzupzup.backend.auth.presentation.interceptor;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AdminCheckInterceptorE2ETest extends E2EHelper {

    private static final String SESSION_ID = "SESSION";

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인하지 않은 상태에서 관리자 전용 API 호출시 401 에러가 발생한다")
    void adminCheckInterceptor_NotLoggedIn() {
        // Given - 로그인하지 않은 상태

        // When & Then
        given()
        .when()
                .get("/admin/organizations")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("status", equalTo(401))
                .body("code", equalTo("A04"));
    }

    @Test
    @DisplayName("로그인 후 관리자 전용 API 호출이 성공한다")
    void adminCheckInterceptor_LoggedInSuccess() {
        // Given - 관리자 로그인
        Password password = new Password("password123");
        Admin admin = new Admin(new LoginId("testAdmin"), passwordEncoder.encode(password), new AdminName("testName"));
        adminRepository.save(admin);

        Organization organization = OrganizationFixture.createByName("테스트 조직");
        organizationRepository.save(organization);
        
        LoginRequest loginRequest = new LoginRequest("testAdmin", "password123");
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
                .cookie(SESSION_ID, sessionCookie)
        .when()
                .get("/admin/organizations")
        .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 관리자 ID로 세션이 설정된 경우 401 에러가 발생한다")
    void adminCheckInterceptor_AdminNotExists() {
        // Given - 관리자 생성 후 삭제하여 세션만 남겨둔 상태
        Password password = new Password("password123");
        Admin admin = new Admin(new LoginId("testAdmin"), passwordEncoder.encode(password), new AdminName("testName"));
        Admin savedAdmin = adminRepository.save(admin);
        
        LoginRequest loginRequest = new LoginRequest("testAdmin", "password123");
        String sessionCookie = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/admin/login")
        .then()
                .extract()
                .cookie(SESSION_ID);

        // 관리자 삭제 (세션은 유지됨)
        adminRepository.delete(savedAdmin);

        // When & Then
        given()
                .cookie(SESSION_ID, sessionCookie)
        .when()
                .get("/admin/organizations")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("status", equalTo(401))
                .body("code", equalTo("A04"));
    }

    @Test
    @DisplayName("로그인 및 회원가입 API는 인터셉터에서 제외된다")
    void adminCheckInterceptor_ExcludePathsWork() {
        // Given
        LoginRequest loginRequest = new LoginRequest("nonUser", "password123");

        // When & Then - 로그인 API는 인터셉터를 거치지 않아야 함
        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/admin/login")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value()) // 사용자가 없어서 404이지, 401이 아님
                .body("status", equalTo(404))
                .body("code", equalTo("G01"));
    }
}
