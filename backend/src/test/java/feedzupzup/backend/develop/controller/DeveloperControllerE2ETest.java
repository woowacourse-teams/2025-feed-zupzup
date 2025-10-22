package feedzupzup.backend.develop.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.application.PasswordEncoder;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.develop.dto.UpdateAdminPasswordRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

class DeveloperControllerE2ETest extends E2EHelper {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${developer.secret-password}")
    private String developerSecretPassword;

    @Test
    @DisplayName("개발자가 관리자 비밀번호 변경에 성공한다")
    void changePassword_success() {
        // given
        Admin admin = AdminFixture.create();
        Admin savedAdmin = adminRepository.save(admin);
        String originalPassword = savedAdmin.getPasswordValue();

        UpdateAdminPasswordRequest request = new UpdateAdminPasswordRequest(
                developerSecretPassword,
                savedAdmin.getId(),
                "newPassword123"  // 변경할 새 비밀번호
        );

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .patch("/develop/change-password")
        .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());

        // 비밀번호가 변경되었는지 확인
        Admin updatedAdmin = adminRepository.findById(savedAdmin.getId()).get();
        assertThat(updatedAdmin.getPasswordValue()).isNotEqualTo(originalPassword);
    }

    @Test
    @DisplayName("잘못된 개발자 비밀번호로 요청 시 401 에러가 발생한다")
    void changePassword_unauthorized() {
        // given
        Admin admin = AdminFixture.create();
        Admin savedAdmin = adminRepository.save(admin);

        UpdateAdminPasswordRequest request = new UpdateAdminPasswordRequest(
                "wrong-developer-password",  // 잘못된 개발자 인증 비밀번호
                savedAdmin.getId(),
                "newPassword123"
        );

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .patch("/develop/change-password")
        .then()
                .log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("status", equalTo(401));
    }

    @Test
    @DisplayName("존재하지 않는 관리자 ID로 비밀번호 변경 시 404 에러가 발생한다")
    void changePassword_notFound() {
        // given
        UpdateAdminPasswordRequest request = new UpdateAdminPasswordRequest(
                developerSecretPassword,
                999L,  // 존재하지 않는 ID
                "newPassword123"
        );

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .patch("/develop/change-password")
        .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo(404));
    }

    @Test
    @DisplayName("어드민이 비밀번호를 변경하고, 변경한 비밀번호로 접속할 수 있어야 한다.")
    void changePassword_and_re_login() {
        // given - 암호화된 비밀번호를 가진 Admin 생성 및 저장
        final String loginId = "testAdmin";
        final String originalPassword = "password123";
        final String newPassword = "newPassword123";

        final Password password = new Password(originalPassword);
        Admin admin = new Admin(
                new LoginId(loginId),
                passwordEncoder.encode(password),
                new AdminName("testName")
        );
        Admin savedAdmin = adminRepository.save(admin);

        // 원래 비밀번호로 로그인 성공 확인
        LoginRequest originalLoginRequest = new LoginRequest(loginId, originalPassword);
        given()
                .contentType(ContentType.JSON)
                .body(originalLoginRequest)
        .when()
                .post("/admin/login")
        .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());

        // 개발자 비밀번호 변경 API로 비밀번호 변경
        UpdateAdminPasswordRequest changePasswordRequest = new UpdateAdminPasswordRequest(
                developerSecretPassword,
                savedAdmin.getId(),
                newPassword
        );

        given()
                .contentType(ContentType.JSON)
                .body(changePasswordRequest)
        .when()
                .patch("/develop/change-password")
        .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());

        // when & then - 새로운 비밀번호로 로그인 성공
        LoginRequest newLoginRequest = new LoginRequest(loginId, newPassword);
        given()
                .contentType(ContentType.JSON)
                .body(newLoginRequest)
        .when()
                .post("/admin/login")
        .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.loginId", equalTo(loginId));

        // 기존 비밀번호로는 로그인 실패
        given()
                .contentType(ContentType.JSON)
                .body(originalLoginRequest)
        .when()
                .post("/admin/login")
        .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}