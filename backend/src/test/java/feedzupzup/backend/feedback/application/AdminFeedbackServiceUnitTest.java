package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.feedback.domain.EmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackDownloadJobStore;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminFeedbackServiceUnitTest {

    @InjectMocks
    private AdminFeedbackService adminFeedbackService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationStatisticRepository organizationStatisticRepository;

    @Mock
    private EmbeddingClusterRepository embeddingClusterRepository;

    @Mock
    private FeedbackDownloadJobStore feedbackDownloadJobStore;

    @Nested
    @DisplayName("피드백 삭제 예외 테스트")
    class DeleteFeedbackExceptionTest {

        @Test
        @DisplayName("관리자가 속한 단체가 아닐 경우, 삭제 시 예외가 발생해야 한다")
        void not_contains_organization_delete_api_then_throw_exception() {
            // given
            Long otherAdminId = 999L;
            Long feedbackId = 1L;

            given(adminRepository.existsFeedbackId(otherAdminId, feedbackId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.delete(otherAdminId, feedbackId))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Nested
    @DisplayName("피드백 조회 예외 테스트")
    class GetFeedbackExceptionTest {

        @Test
        @DisplayName("존재하지 않는 단체를 조회하면 예외를 발생시킨다")
        void getAllFeedbacks_empty_result() {
            // given
            UUID organizationUuid = UUID.randomUUID();
            int size = 10;

            given(organizationRepository.existsOrganizationByUuid(organizationUuid)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.getFeedbackPage(organizationUuid, size, null, null, null))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("댓글 수정 예외 테스트")
    class UpdateCommentExceptionTest {

        @Test
        @DisplayName("단체에 속하지 않은 관리자가 댓글을 수정하려고 한다면 예외가 발생해야 한다")
        void not_contains_organization_admin_request_then_throw_exception() {
            // given
            Long otherAdminId = 999L;
            Long feedbackId = 1L;
            UpdateFeedbackCommentRequest request = new UpdateFeedbackCommentRequest("testComment");

            given(adminRepository.existsFeedbackId(otherAdminId, feedbackId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.updateFeedbackComment(otherAdminId, request, feedbackId))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Nested
    @DisplayName("클러스터 조회 예외 테스트")
    class GetClusterExceptionTest {

        @Test
        @DisplayName("존재하지 않는 조직을 조회하면 예외가 발생한다")
        void getTopClusters_not_found_organization() {
            // given
            UUID nonExistentUuid = UUID.randomUUID();

            given(organizationRepository.existsOrganizationByUuid(nonExistentUuid)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.getTopClusters(nonExistentUuid, 10))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 클러스터를 조회하면 예외가 발생한다")
        void getFeedbacksByClusterId_not_found() {
            // given
            Long nonExistentClusterId = 99999L;

            given(embeddingClusterRepository.findById(nonExistentClusterId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.getFeedbacksByClusterId(nonExistentClusterId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("비동기 다운로드 작업 예외 테스트")
    class AsyncDownloadJobExceptionTest {

        @Test
        @DisplayName("존재하지 않는 단체로 작업 생성 시 예외가 발생한다")
        void createDownloadJob_notFoundOrganization() {
            // given
            UUID nonExistentUuid = UUID.randomUUID();

            given(organizationRepository.existsOrganizationByUuid(nonExistentUuid)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.createDownloadJob(nonExistentUuid))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 작업 ID로 조회 시 예외가 발생한다")
        void getDownloadJobStatus_notFound() {
            // given
            String nonExistentJobId = UUID.randomUUID().toString();

            given(feedbackDownloadJobStore.getById(nonExistentJobId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.getDownloadJobStatus(nonExistentJobId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 작업의 다운로드 URL 요청 시 예외가 발생한다")
        void getDownloadUrl_notFound() {
            // given
            String nonExistentJobId = UUID.randomUUID().toString();

            given(feedbackDownloadJobStore.getById(nonExistentJobId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.getDownloadUrl(nonExistentJobId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
