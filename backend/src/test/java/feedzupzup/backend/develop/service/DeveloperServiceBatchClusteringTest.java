package feedzupzup.backend.develop.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class DeveloperServiceBatchClusteringTest extends ServiceIntegrationHelper {

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FeedbackEmbeddingClusterRepository feedbackEmbeddingClusterRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @MockitoBean
    private FeedbackClusteringService feedbackClusteringService;

    @Test
    @DisplayName("배치 클러스터링이 성공적으로 수행된다")
    void batchClustering_success() {
        // given
        Organization org1 = OrganizationFixture.createByName("조직1");
        organizationRepository.save(org1);

        OrganizationCategory category1 = OrganizationCategoryFixture.createOrganizationCategory(org1);
        organizationCategoryRepository.save(category1);

        // 배치 크기를 50으로 설정했으므로 50개 이상의 피드백을 생성하여 페이지네이션 테스트
        for (int i = 1; i <= 75; i++) {
            Feedback feedback = FeedbackFixture.createFeedback(org1, "피드백" + i, category1);
            feedbackRepository.save(feedback);
        }

        // when
        developerService.batchClustering();

        // then - 75개의 피드백이 모두 클러스터링되어야 함
        verify(feedbackClusteringService, times(75)).cluster(anyLong());
    }

    @Test
    @DisplayName("클러스터링 실패 시에도 다른 피드백 처리를 계속한다")
    void batchClustering_continueOnFailure() {
        // given
        Organization org = OrganizationFixture.createByName("조직");
        organizationRepository.save(org);

        OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(org);
        organizationCategoryRepository.save(category);

        Feedback feedback1 = FeedbackFixture.createFeedback(org, "피드백1", category);
        Feedback feedback2 = FeedbackFixture.createFeedback(org, "피드백2", category);
        Feedback feedback3 = FeedbackFixture.createFeedback(org, "피드백3", category);
        
        feedbackRepository.save(feedback1);
        feedbackRepository.save(feedback2);
        feedbackRepository.save(feedback3);

        // 첫 번째 피드백 클러스터링은 실패
        doThrow(new RuntimeException("클러스터링 실패"))
                .when(feedbackClusteringService).cluster(feedback1.getId());

        // when
        developerService.batchClustering();

        // then - 실패에도 불구하고 모든 피드백에 대해 시도
        verify(feedbackClusteringService, times(3)).cluster(anyLong());
    }

    @Test
    @DisplayName("페이지네이션으로 대용량 피드백을 처리한다")
    void batchClustering_paginationHandling() {
        // given
        Organization org = OrganizationFixture.createByName("조직");
        organizationRepository.save(org);

        OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(org);
        organizationCategoryRepository.save(category);

        // 배치 크기(50)보다 많은 120개의 피드백 생성 (3페이지)
        for (int i = 1; i <= 120; i++) {
            Feedback feedback = FeedbackFixture.createFeedback(org, "피드백" + i, category);
            feedbackRepository.save(feedback);
        }

        // when
        developerService.batchClustering();

        // then - 모든 120개 피드백이 처리되어야 함
        verify(feedbackClusteringService, times(120)).cluster(anyLong());
    }

    @Test
    @DisplayName("조직이 없는 경우 클러스터링을 수행하지 않는다")
    void batchClustering_noOrganizations() {
        // given - 조직이 없는 상태

        // when
        developerService.batchClustering();

        // then
        verify(feedbackClusteringService, times(0)).cluster(anyLong());
    }

    @Test
    @DisplayName("organizationBatchSize가 20개 이상일 때 페이지네이션으로 처리한다")
    void batchClustering_multipleOrganizationPages() {
        // given - 25개 조직 생성 (organizationBatchSize=20이므로 2페이지)
        for (int orgNum = 1; orgNum <= 25; orgNum++) {
            Organization org = OrganizationFixture.createByName("조직" + orgNum);
            organizationRepository.save(org);

            OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(org);
            organizationCategoryRepository.save(category);

            System.out.println(orgNum);
            // 각 조직마다 3개의 피드백 생성
            for (int feedbackNum = 1; feedbackNum <= 3; feedbackNum++) {
                Feedback feedback = FeedbackFixture.createFeedback(org, "피드백" + feedbackNum, category);
                feedbackRepository.save(feedback);
            }
            List<Feedback> byOrganization = feedbackRepository.findByOrganization(org);
            System.out.println(org.getId() + " : " +byOrganization);
        }

        // when
        developerService.batchClustering();

        // then - 25개 조직 × 3개 피드백 = 75개 피드백이 모두 처리되어야 함
        verify(feedbackClusteringService, times(75)).cluster(anyLong());
    }
}
