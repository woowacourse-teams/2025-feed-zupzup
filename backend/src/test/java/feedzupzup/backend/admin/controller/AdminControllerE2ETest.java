package feedzupzup.backend.admin.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.application.PasswordEncoder;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.config.E2EHelper;
import feedzupzup.backend.notification.domain.NotificationToken;
import feedzupzup.backend.notification.domain.NotificationTokenRepository;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AdminControllerE2ETest extends E2EHelper {

    private static final String SESSION_ID = "JSESSIONID";

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Test
    @DisplayName("관리자가 회원탈퇴를 성공적으로 수행한다")
    void withdraw_success() {
        // given
        Password password = new Password("password123");
        Admin admin = new Admin(new LoginId("testAdmin"), passwordEncoder.encode(password), new AdminName("testName"));
        adminRepository.save(admin);

        NotificationToken notificationToken = new NotificationToken(admin, "test-token");
        notificationTokenRepository.save(notificationToken);

        Organization organization = OrganizationFixture.createAllBlackBox();
        Organization saved = organizationRepository.save(organization);

        Organizer organizer = new Organizer(saved, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        LoginRequest loginRequest = new LoginRequest("testAdmin", "password123");
        String sessionCookie = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
        .when()
                .post("/admin/login")
        .then()
                .extract()
                .cookie(SESSION_ID);

        // when & then
        given()
                .cookie(SESSION_ID, sessionCookie)
        .when()
                .delete("/admin")
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("로그인하지 않은 상태에서 회원탈퇴 요청시 401 에러가 발생한다")
    void withdraw_unauthorized() {
        // when & then
        given()
        .when()
                .delete("/admin")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("status", equalTo(401))
                .body("code", equalTo("A04"));
    }
}
