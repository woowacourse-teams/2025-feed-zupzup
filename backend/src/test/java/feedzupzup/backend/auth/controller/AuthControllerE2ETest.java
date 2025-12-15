package feedzupzup.backend.auth.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.application.PasswordEncoder;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.auth.dto.request.SignUpRequest;
import feedzupzup.backend.config.E2EHelper;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@Disabled
class AuthControllerE2ETest extends E2EHelper {

    private static final String SESSION_ID = "SESSION";

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("정상적인 회원가입 요청시 회원가입이 성공하고 세션이 생성된다")
    void signUp_Success() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "password123", "testName");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/admin/sign-up")
        .then()
                .log().all();
    }

    @Test
    @DisplayName("중복된 로그인 ID로 회원가입시 400 에러가 발생한다")
    void signUp_DuplicateLoginId() {
        // Given
        Admin existingAdmin = new Admin(new LoginId("testId"), new EncodedPassword("password123"), new AdminName("existName"));
        adminRepository.save(existingAdmin);
        
        SignUpRequest request = new SignUpRequest("testId", "password123", "testName");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/admin/sign-up")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .body("status", equalTo(400))
                .body("code", equalTo("A05"))
                .body("message", equalTo("이미 존재하는 로그인 ID입니다."));
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호 형식으로 회원가입시 400 에러가 발생한다")
    void signUp_InvalidPasswordFormat() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "password한글", "testName");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/admin/sign-up")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .body("status", equalTo(400))
                .body("code", equalTo("A02"))
                .body("message", equalTo("비밀번호는 공백을 포함하지 않고 5글자 이상이어야 합니다."));
    }

    @Test
    @DisplayName("짧은 비밀번호로 회원가입시 400 에러가 발생한다")
    void signUp_ShortPassword() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "test", "testName");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/admin/sign-up")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(400))
                .body("code", equalTo("A02"))
                .body("message", equalTo("비밀번호는 공백을 포함하지 않고 5글자 이상이어야 합니다."));
    }

    @Test
    @DisplayName("유효하지 않은 로그인 ID 형식으로 회원가입시 400 에러가 발생한다")
    void signUp_InvalidLoginIdFormat() {
        // Given
        SignUpRequest request = new SignUpRequest("test@#", "password123", "testName");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/admin/sign-up")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(400))
                .body("code", equalTo("A01"));
    }

    @Test
    @DisplayName("유효하지 않은 관리자 이름 형식으로 회원가입시 400 에러가 발생한다")
    void signUp_InvalidAdminNameFormat() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "password123", "test@#");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/admin/sign-up")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(400))
                .body("code", equalTo("A03"));
    }

    @Test
    @DisplayName("정상적인 로그인 요청시 로그인이 성공하고 세션이 생성된다")
    void login_Success() {
        // Given
        final Password password = new Password("password123");

        Admin admin = new Admin(new LoginId("testId"), passwordEncoder.encode(password), new AdminName("testName"));
        adminRepository.save(admin);
        
        LoginRequest request = new LoginRequest("testId", "password123");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/admin/login")
        .then()
                .statusCode(HttpStatus.OK.value())
                .cookie(SESSION_ID, notNullValue())
                .body("data.loginId", equalTo("testId"))
                .body("data.adminName", equalTo("testName"))
                .body("data.adminId", notNullValue());
    }

    @Test
    @DisplayName("존재하지 않는 로그인 ID로 로그인시 404 에러가 발생한다")
    void login_LoginIdNotFound() {
        // Given
        LoginRequest request = new LoginRequest("noExistId", "password");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/admin/login")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo(404))
                .body("code", equalTo("G01"));
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 400 에러가 발생한다")
    void login_InvalidPassword() {
        // Given
        Password password = new Password("correctPassword123");
        Admin admin = new Admin(new LoginId("testId"), passwordEncoder.encode(password), new AdminName("testName"));
        adminRepository.save(admin);
        
        LoginRequest request = new LoginRequest("testId", "wrongPassword123");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/admin/login")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo(400))
                .body("code", equalTo("A06"));
    }

    @Test
    @DisplayName("로그인 후 로그아웃이 성공한다")
    void logout_Success() {
        // Given - 먼저 로그인하여 세션 생성
        Password password = new Password("password123");
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
                .cookie(SESSION_ID, sessionCookie)
        .when()
                .post("/admin/logout")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data", equalTo("로그아웃이 완료되었습니다."));
    }

    @Test
    @DisplayName("로그인 후 관리자 정보 조회가 성공한다")
    void getAdminInfo_Success() {
        // Given - 먼저 로그인하여 세션 생성
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
                .cookie(SESSION_ID, sessionCookie)
        .when()
                .get("/admin/me")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.loginId", equalTo("testId"))
                .body("data.adminName", equalTo("testName"))
                .body("data.adminId", notNullValue());
    }

    @Test
    @DisplayName("로그인하지 않은 상태에서 관리자 정보 조회시 401 에러가 발생한다")
    void getAdminInfo_NotLoggedIn() {
        // When & Then
        given()
        .when()
                .get("/admin/me")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("status", equalTo(401))
                .body("code", equalTo("A04"));
    }
}
