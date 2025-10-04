package feedzupzup.backend.feedback.domain;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.RepositoryHelper;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FeedbackRepositoryTest extends RepositoryHelper {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    private Organization organization;
    private OrganizationCategory organizationCategory;

    @BeforeEach
    void setUp() {
        organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        organizationCategory = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, SUGGESTION));
    }

    @Nested
    @DisplayName("클러스터 대표 피드백 조회 테스트")
    class FindAllRepresentativeFeedbackPerClusterTest {

        @Test
        @DisplayName("조직의 모든 클러스터 대표 피드백을 조회한다")
        void findAllRepresentativeFeedbackPerCluster_success() {
            // given
            final UUID clusterId1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final UUID clusterId2 = UUID.fromString("22222222-2222-2222-2222-222222222222");

            // 첫 번째 클러스터의 피드백 3개 (가장 오래된 것이 대표가 됨)
            final Feedback feedback1_1 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "첫 번째 클러스터 대표", organizationCategory, clusterId1);
            final Feedback feedback1_2 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "첫 번째 클러스터 추가1", organizationCategory, clusterId1);
            final Feedback feedback1_3 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "첫 번째 클러스터 추가2", organizationCategory, clusterId1);

            // 두 번째 클러스터의 피드백 2개
            final Feedback feedback2_1 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "두 번째 클러스터 대표", organizationCategory, clusterId2);
            final Feedback feedback2_2 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "두 번째 클러스터 추가", organizationCategory, clusterId2);

            feedbackRepository.save(feedback1_1);
            feedbackRepository.save(feedback1_2);
            feedbackRepository.save(feedback1_3);
            feedbackRepository.save(feedback2_1);
            feedbackRepository.save(feedback2_2);

            // when
            final List<ClusterRepresentativeFeedback> result = feedbackRepository
                    .findAllRepresentativeFeedbackPerCluster(organization.getUuid());

            // then
            ClusterRepresentativeFeedback cluster1 = result.stream()
                    .filter(cluster -> cluster.clusterUuid().equals(clusterId1))
                    .findFirst()
                    .orElseThrow();
            ClusterRepresentativeFeedback cluster2 = result.stream()
                    .filter(cluster -> cluster.clusterUuid().equals(clusterId2))
                    .findFirst()
                    .orElseThrow();
            assertAll(
                    () -> assertThat(result).hasSize(2),
                    () -> {
                        assertThat(cluster1.content()).isEqualTo("첫 번째 클러스터 대표");
                        assertThat(cluster1.totalCount()).isEqualTo(3);
                    },
                    () -> {
                        assertThat(cluster2.content()).isEqualTo("두 번째 클러스터 대표");
                        assertThat(cluster2.totalCount()).isEqualTo(2);
                    }
            );
        }

        @Test
        @DisplayName("다른 조직의 피드백은 결과에 포함되지 않는다")
        void findAllRepresentativeFeedbackPerCluster_excludes_other_organization() {
            // given
            final Organization otherOrganization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            final OrganizationCategory otherOrgCategory = organizationCategoryRepository.save(
                    OrganizationCategoryFixture.createOrganizationCategory(otherOrganization, SUGGESTION));

            final UUID clusterId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            // 내 조직의 피드백
            final Feedback myFeedback = FeedbackFixture.createFeedbackWithCluster(
                    organization, "내 조직 피드백", organizationCategory, clusterId);

            // 다른 조직의 피드백 (같은 클러스터 ID)
            final Feedback otherFeedback = FeedbackFixture.createFeedbackWithCluster(
                    otherOrganization, "다른 조직 피드백", otherOrgCategory, clusterId);

            feedbackRepository.save(myFeedback);
            feedbackRepository.save(otherFeedback);

            // when
            final List<ClusterRepresentativeFeedback> result = feedbackRepository
                    .findAllRepresentativeFeedbackPerCluster(organization.getUuid());

            // then
            assertAll(
                    () -> assertThat(result).hasSize(1),
                    () -> assertThat(result.get(0).content()).isEqualTo("내 조직 피드백"),
                    () -> assertThat(result.get(0).totalCount()).isEqualTo(1)
            );
        }
    }
}
