package feedzupzup.backend.develop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.develop.dto.UpdateAdminPasswordRequest;
import feedzupzup.backend.feedback.domain.EmbeddingCluster;
import feedzupzup.backend.feedback.domain.EmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeveloperServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FeedbackEmbeddingClusterRepository feedbackEmbeddingClusterRepository;

    @Autowired
    private EmbeddingClusterRepository embeddingClusterRepository;

    @Test
    @DisplayName("관리자 비밀번호 변경에 성공한다")
    void changePassword_success() {
        // given
        Admin admin = AdminFixture.create();
        Admin savedAdmin = adminRepository.save(admin);
        String originalPassword = savedAdmin.getPasswordValue();

        UpdateAdminPasswordRequest request = new UpdateAdminPasswordRequest(
                "any-value",
                savedAdmin.getLoginId().value(),
                "newPassword789"
        );

        // when
        developerService.changePassword(request);

        // then
        Admin updatedAdmin = adminRepository.findById(savedAdmin.getId()).get();
        assertThat(updatedAdmin.getPasswordValue()).isNotEqualTo(originalPassword);
    }

    @Test
    @DisplayName("존재하지 않는 관리자 ID로 비밀번호 변경 시 예외가 발생한다")
    void changePassword_fail_notFoundAdmin() {
        // given
        UpdateAdminPasswordRequest request = new UpdateAdminPasswordRequest(
                "any-value",
                "999L",
                "newPassword789"
        );

        // when & then
        assertThatThrownBy(() -> developerService.changePassword(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("존재하지 않는 adminId 입니다.");
    }

    @Test
    @DisplayName("배치 클러스터링을 실행한다")
    void batchClustering() {
        // when & then
        assertThatCode(() -> developerService.batchClustering()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("배치 클러스터링 - 피드백이 없을 때 정상 동작한다")
    void batchClustering_noFeedbacks() {
        // when & then
        assertThatCode(() -> developerService.batchClustering()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("배치 클러스터링 - 여러 피드백을 처리한다")
    void batchClustering_multipleFeedbacks() {
        // given
        Organization organization = organizationRepository.save(OrganizationFixture.create(0));
        OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization)
        );

        feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "첫 번째 피드백", category)
        );
        feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "두 번째 피드백", category)
        );
        feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "세 번째 피드백", category)
        );

        // Mock AI API
        when(embeddingExtractor.extract(anyString())).thenReturn(new double[]{0.1, 0.2, 0.3});
        when(clusterLabelGenerator.generate(any())).thenReturn("테스트 라벨");

        // when & then
        assertThatCode(() -> developerService.batchClustering()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("배치 클러스터링 - 이미 클러스터링된 피드백은 건너뛴다")
    void batchClustering_skipAlreadyClusteredFeedbacks() {
        // given
        Organization organization = organizationRepository.save(OrganizationFixture.create(0));
        OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization)
        );

        Feedback feedback1 = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "첫 번째 피드백", category)
        );
        feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "두 번째 피드백", category)
        );

        // feedback1은 이미 클러스터링됨
        EmbeddingCluster embeddingCluster = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
        double[] embedding = new double[]{0.1, 0.2, 0.3};
        FeedbackEmbeddingCluster cluster = FeedbackEmbeddingCluster.createNewCluster(
                embedding, feedback1, embeddingCluster
        );
        feedbackEmbeddingClusterRepository.save(cluster);

        long initialClusterCount = feedbackEmbeddingClusterRepository.count();

        // Mock AI API
        when(embeddingExtractor.extract(anyString())).thenReturn(new double[]{0.1, 0.2, 0.3});
        when(clusterLabelGenerator.generate(any())).thenReturn("테스트 라벨");

        // when
        developerService.batchClustering();

        // then - feedback2가 새로 클러스터링되어 count가 증가했는지 확인
        assertThat(feedbackEmbeddingClusterRepository.count()).isGreaterThan(initialClusterCount);
    }

}
