package feedzupzup.backend.qr.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
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
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import feedzupzup.backend.qr.domain.QR;
import feedzupzup.backend.qr.repository.QRRepository;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@Disabled
class QRControllerE2ETest extends E2EHelper {

    private static final String SESSION_ID = "SESSION";

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private QRRepository qrRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String sessionCookie;

    private Admin admin;

    @BeforeEach
    void setUpAuth() {
        final Password password = new Password("password123");
        admin = new Admin(new LoginId("testId"), passwordEncoder.encode(password),
                new AdminName("testName"));
        adminRepository.save(admin);

        final LoginRequest loginRequest = new LoginRequest("testId", "password123");
        sessionCookie = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/admin/login")
                .then()
                .extract()
                .cookie(SESSION_ID);
    }

    @Nested
    @DisplayName("QR 코드 조회 테스트")
    class GetQRTest {

        @Test
        @DisplayName("조직 UUID로 QR 코드를 성공적으로 조회한다")
        void getQR_success() {
            // given

            final Organization organization = OrganizationFixture.createAllBlackBox();
            final Organization savedOrganization = organizationRepository.save(organization);

            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            final QR qr = new QR("https://example.com/qr-image.png", savedOrganization);
            qrRepository.save(qr);

            // when & then
            given()
                    .log().all()
                    .cookie(SESSION_ID, sessionCookie)
                    .when()
                    .get("/admin/organizations/{organizationUuid}/qr-code", savedOrganization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .contentType(ContentType.JSON)
                    .body("status", equalTo(200))
                    .body("message", equalTo("OK"))
                    .body("data", notNullValue())
                    .body("data.imageUrl", equalTo("https://example.com/qr-image.png"))
                    .body("data.siteUrl", containsString(savedOrganization.getUuid().toString()));
        }

        @Test
        @DisplayName("존재하지 않는 조직 UUID로 QR 조회 시 403 에러를 반환한다")
        void getQR_organization_not_found() {
            // given
            final UUID nonExistentOrganizationUuid = UUID.randomUUID();

            // when & then
            given()
                    .log().all()
                    .cookie(SESSION_ID, sessionCookie)
                    .when()
                    .get("/admin/organizations/{organizationUuid}/qr-code", nonExistentOrganizationUuid)
                    .then().log().all()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("조직은 존재하지만 QR이 없을 때 404 에러를 반환한다")
        void getQR_qr_not_found() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            final Organization savedOrganization = organizationRepository.save(organization);

            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            // when & then
            given()
                    .log().all()
                    .cookie(SESSION_ID, sessionCookie)
                    .when()
                    .get("/admin/organizations/{organizationUuid}/qr-code", savedOrganization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(ContentType.JSON);
        }
    }

    @Nested
    @DisplayName("QR 다운로드 URL 조회 테스트")
    class GetQRDownloadUrlTest {

        @Test
        @DisplayName("존재하지 않는 조직 UUID로 QR 다운로드 URL 조회 시 403 에러를 반환한다")
        void getQRDownloadUrl_organization_not_found() {
            // given
            final UUID nonExistentOrganizationUuid = UUID.randomUUID();

            // when & then
            given()
                    .log().all()
                    .cookie(SESSION_ID, sessionCookie)
                    .when()
                    .get("/admin/organizations/{organizationUuid}/qr-code/download-url", nonExistentOrganizationUuid)
                    .then().log().all()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .contentType(ContentType.JSON);
        }

        @Test
        @DisplayName("조직은 존재하지만 QR이 없을 때 다운로드 URL 조회 시 404 에러를 반환한다")
        void getQRDownloadUrl_qr_not_found() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            final Organization savedOrganization = organizationRepository.save(organization);

            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            // when & then
            given()
                    .log().all()
                    .cookie(SESSION_ID, sessionCookie)
                    .when()
                    .get("/admin/organizations/{organizationUuid}/qr-code/download-url", savedOrganization.getUuid())
                    .then().log().all()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .contentType(ContentType.JSON);
        }
    }
}
