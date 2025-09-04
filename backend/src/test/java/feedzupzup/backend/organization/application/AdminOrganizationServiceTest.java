package feedzupzup.backend.organization.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminUpdateOrganizationResponse;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.qr.domain.QR;
import feedzupzup.backend.qr.repository.QRRepository;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminOrganizationServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private AdminOrganizationService adminOrganizationService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private QRRepository qrRepository;

    @Test
    @DisplayName("정상적인 admin이 조직을 생성하려고 할 때, 생성할 수 있어야 한다.")
    void save_success_case() {
        CreateOrganizationRequest createOrganizationRequest =
                new CreateOrganizationRequest(
                        "우테코",
                        Set.of("건의", "신고")
                );

        final Admin savedAdmin = createAndSaveAdmin();

        final AdminCreateOrganizationResponse response = adminOrganizationService.createOrganization(
                createOrganizationRequest, savedAdmin.getId());

        assertThat(response.organizationUuid()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 admin이 조직을 생성하려고 한다면, 예외가 발생해야 한다.")
    void invalid_admin_request_then_throw_exception() {
        CreateOrganizationRequest createOrganizationRequest =
                new CreateOrganizationRequest(
                        "우테코",
                        Set.of("건의", "신고")
                );

        assertThatThrownBy(() -> adminOrganizationService.createOrganization(createOrganizationRequest, 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 생성하려고 한다면, 예외가 발생해야 한다.")
    void invalid_category_then_throw_exception() {
        CreateOrganizationRequest createOrganizationRequest =
                new CreateOrganizationRequest(
                        "우테코",
                        Set.of("크롱크롱", "대나무헬리콥터")
                );
        final Admin admin = createAndSaveAdmin();
        assertThatThrownBy(() -> adminOrganizationService.createOrganization(createOrganizationRequest, admin.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("권한이 있는 admin이 본인의 단체를 수정할 수 있어야 한다")
    void update_organization_success() {
        // given
        CreateOrganizationRequest request =
                new CreateOrganizationRequest(
                        "우테코",
                        Set.of("건의", "신고")
                );

        final Admin savedAdmin = createAndSaveAdmin();

        final AdminCreateOrganizationResponse createResponse = adminOrganizationService.createOrganization(
                request, savedAdmin.getId());

        UpdateOrganizationRequest updateOrganizationRequest = new UpdateOrganizationRequest(
                "우테코코코", Set.of("기타", "칭찬", "정보공유")
        );
        final UUID organizationUuid = UUID.fromString(createResponse.organizationUuid());

        // when
        final AdminUpdateOrganizationResponse updateResponse = adminOrganizationService.updateOrganization(
                organizationUuid,
                updateOrganizationRequest
        );

        // then
        assertThat(updateResponse.updateName()).isEqualTo("우테코코코");
        assertThat(updateResponse.updateCategories()).containsExactlyInAnyOrder("기타", "칭찬", "정보공유");
    }

    @Test
    @DisplayName("조직을 성공적으로 삭제할 수 있어야 한다")
    void delete_organization_success() {
        // given
        final Admin admin = createAndSaveAdmin();
        final Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        organizerRepository.save(new Organizer(organization, admin, OrganizerRole.OWNER));

        // when
        adminOrganizationService.deleteOrganization(organization.getUuid());

        // then
        assertThat(organizationRepository.findByUuid(organization.getUuid())).isEmpty();
        assertThat(organizerRepository.findByOrganizationId(organization.getId())).isEmpty();
    }

    @Test
    @DisplayName("조직과 연관된 모든 데이터가 함께 삭제되어야 한다")
    void delete_organization_with_all_related_data() {
        // given
        final Admin admin = createAndSaveAdmin();
        final Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        organizerRepository.save(new Organizer(organization, admin, OrganizerRole.OWNER));

        // 연관 데이터 생성
        final OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(organization, Category.SUGGESTION);
        organizationCategoryRepository.save(category);

        final Feedback feedback = FeedbackFixture.createFeedbackWithContent(organization, "테스트 피드백", category);
        feedbackRepository.save(feedback);

        final QR qr = new QR("test-image-url", organization);
        qrRepository.save(qr);

        final Long organizationId = organization.getId();

        // when
        adminOrganizationService.deleteOrganization(organization.getUuid());

        // then - 조직과 연관된 모든 데이터가 삭제되어야 함
        assertThat(organizationRepository.findByUuid(organization.getUuid())).isEmpty();
        assertThat(organizerRepository.findByOrganizationId(organizationId)).isEmpty();
        assertThat(organizationCategoryRepository.findById(category.getId())).isEmpty();
        assertThat(feedbackRepository.findById(feedback.getId())).isEmpty();
        assertThat(qrRepository.findById(qr.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 조직을 삭제하려 할 때 예외가 발생해야 한다")
    void delete_nonexistent_organization_should_throw_exception() {
        // given
        final UUID nonexistentUuid = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> adminOrganizationService.deleteOrganization(nonexistentUuid))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("해당 UUID를 가진 단체는 존재하지 않습니다");
    }

    private Admin createAndSaveAdmin() {
        final Admin admin = AdminFixture.create();
        return adminRepository.save(admin);
    }
}
