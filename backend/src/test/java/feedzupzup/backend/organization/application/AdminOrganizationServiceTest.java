package feedzupzup.backend.organization.application;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminInquireOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminUpdateOrganizationResponse;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import feedzupzup.backend.qr.domain.QR;
import feedzupzup.backend.qr.repository.QRRepository;
import java.util.List;
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
                        List.of("건의", "신고")
                );

        final Admin savedAdmin = createAndSaveAdmin();

        final AdminCreateOrganizationResponse response = adminOrganizationService.createOrganization(
                createOrganizationRequest, savedAdmin.getId());

        assertThat(response.organizationUuid()).isNotNull();
    }

    @Test
    @DisplayName("권한이 있는 admin이 본인의 단체를 수정할 수 있어야 한다")
    void update_organization_success() {
        // given
        CreateOrganizationRequest request =
                new CreateOrganizationRequest(
                        "우테코",
                        List.of("건의", "신고")
                );

        final Admin savedAdmin = createAndSaveAdmin();

        final AdminCreateOrganizationResponse createResponse = adminOrganizationService.createOrganization(
                request, savedAdmin.getId());

        UpdateOrganizationRequest updateOrganizationRequest = new UpdateOrganizationRequest(
                "우테코코코", List.of("기타", "칭찬", "정보공유")
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
        final OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(organization,
                Category.SUGGESTION);
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
    @DisplayName("조직 삭제 시 연관된 피드백들도 함께 삭제되어야 한다")
    void delete_organization_then_related_feedbacks_should_be_deleted() {
        // given
        final Admin admin = createAndSaveAdmin();
        final Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        organizerRepository.save(new Organizer(organization, admin, OrganizerRole.OWNER));

        final OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(organization,
                Category.SUGGESTION);
        final OrganizationCategory savedCategory = organizationCategoryRepository.save(category);

        final Feedback feedback = FeedbackFixture.createFeedbackWithContent(organization, "테스트 피드백", savedCategory);
        final Feedback savedFeedback = feedbackRepository.save(feedback);

        // 삭제 전 상태 확인
        assertThat(savedFeedback.getOrganizationCategory()).isNotNull();
        assertThat(savedFeedback.getOrganizationCategory().getId()).isEqualTo(savedCategory.getId());

        // when - 조직 삭제 (소프트 삭제)
        adminOrganizationService.deleteOrganization(organization.getUuid());

        // then - 피드백도 함께 소프트 삭제되어야 함
        assertThat(feedbackRepository.findById(savedFeedback.getId())).isEmpty();
    }

    @Test
    @DisplayName("조직 수정 시 카테고리가 정렬되어 반환되어야 한다")
    void update_organization_with_sorted_categories() {
        // given
        CreateOrganizationRequest request =
                new CreateOrganizationRequest(
                        "우테코",
                        List.of("건의", "신고")
                );

        final Admin savedAdmin = createAndSaveAdmin();

        final AdminCreateOrganizationResponse createResponse = adminOrganizationService.createOrganization(
                request, savedAdmin.getId());

        // 정렬되지 않은 순서로 카테고리 입력: 칭찬, 기타, 정보공유
        UpdateOrganizationRequest updateOrganizationRequest = new UpdateOrganizationRequest(
                "우테코코코", List.of("칭찬", "기타", "정보공유")
        );
        final UUID organizationUuid = UUID.fromString(createResponse.organizationUuid());

        // when
        final AdminUpdateOrganizationResponse updateResponse = adminOrganizationService.updateOrganization(
                organizationUuid,
                updateOrganizationRequest
        );

        // then - 한글 사전순으로 정렬되어 반환: 기타, 정보공유, 칭찬
        assertThat(updateResponse.updateCategories()).containsExactly("기타", "정보공유", "칭찬");
    }

    @Test
    @DisplayName("조직 조회 시 완료 건수와 대기 건수를 정확하게 반환해야 한다")
    void get_organizations_info_with_confirmed_and_waiting_count() {
        // given
        final Admin admin = createAndSaveAdmin();
        final Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        organizerRepository.save(new Organizer(organization, admin, OrganizerRole.OWNER));

        final OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(organization,
                Category.SUGGESTION);
        organizationCategoryRepository.save(category);

        // WAITING 상태의 피드백 3개 생성
        feedbackRepository.save(FeedbackFixture.createFeedbackWithStatus(organization, ProcessStatus.WAITING, category));
        feedbackRepository.save(FeedbackFixture.createFeedbackWithStatus(organization, ProcessStatus.WAITING, category));
        feedbackRepository.save(FeedbackFixture.createFeedbackWithStatus(organization, ProcessStatus.WAITING, category));

        // CONFIRMED 상태의 피드백 2개 생성
        feedbackRepository.save(FeedbackFixture.createFeedbackWithStatus(organization, ProcessStatus.CONFIRMED, category));
        feedbackRepository.save(FeedbackFixture.createFeedbackWithStatus(organization, ProcessStatus.CONFIRMED, category));

        // when
        final List<AdminInquireOrganizationResponse> responses = adminOrganizationService.getOrganizationsInfo(
                admin.getId());

        // then
        assertThat(responses).hasSize(1);
        final AdminInquireOrganizationResponse response = responses.get(0);
        assertThat(response.confirmedCount()).isEqualTo(2L);
        assertThat(response.waitingCount()).isEqualTo(3L);
    }

    private Admin createAndSaveAdmin() {
        final Admin admin = AdminFixture.create();
        return adminRepository.save(admin);
    }
}
