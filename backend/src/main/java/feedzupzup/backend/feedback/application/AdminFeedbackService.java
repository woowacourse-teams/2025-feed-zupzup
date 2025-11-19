package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.organization.domain.StatusTransition.DELETED_CONFIRMED;
import static feedzupzup.backend.organization.domain.StatusTransition.DELETED_WAITING;
import static feedzupzup.backend.organization.domain.StatusTransition.WAITING_TO_CONFIRMED;

import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.feedback.domain.ClusterInfo;
import feedzupzup.backend.feedback.domain.EmbeddingCluster;
import feedzupzup.backend.feedback.domain.EmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackDownloadJobStore;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.service.sort.FeedbackSortStrategy;
import feedzupzup.backend.feedback.domain.service.sort.FeedbackSortStrategyFactory;
import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob;
import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob.DownloadStatus;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.ClusterFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.ClustersResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.feedback.exception.FeedbackException.DownloadJobNotCompletedException;
import feedzupzup.backend.feedback.exception.FeedbackException.DownloadUrlNotGeneratedException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.log.BusinessActionLog;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import feedzupzup.backend.s3.service.S3PresignedDownloadService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminFeedbackService {

    private final AdminRepository adminRepository;
    private final FeedbackRepository feedBackRepository;
    private final FeedbackSortStrategyFactory feedbackSortStrategyFactory;
    private final OrganizationRepository organizationRepository;
    private final OrganizationStatisticRepository organizationStatisticRepository;
    private final EmbeddingClusterRepository embeddingClusterRepository;
    private final FeedbackEmbeddingClusterRepository feedbackEmbeddingClusterRepository;
    private final S3PresignedDownloadService s3PresignedDownloadService;
    private final FeedbackDownloadJobStore feedbackDownloadJobStore;
    private final FeedbackFileDownloadService feedbackFileDownloadService;

    @Transactional
    @BusinessActionLog
    public void delete(
            final Long adminId,
            final Long feedbackId
    ) {
        validateAuthentication(adminId, feedbackId);
        final Feedback feedback = getFeedback(feedbackId);
        handleOrganizationStatistic(feedback);
        feedbackEmbeddingClusterRepository.deleteByFeedback_Id(feedbackId);
        feedBackRepository.deleteById(feedbackId);
    }

    private void handleOrganizationStatistic(final Feedback feedback) {
        if (feedback.getStatus() == ProcessStatus.CONFIRMED) {
            organizationStatisticRepository.updateOrganizationStatisticCounts(
                    feedback.getOrganizationIdValue(),
                    DELETED_CONFIRMED.getTotalAmount(),
                    DELETED_CONFIRMED.getConfirmedAmount(),
                    DELETED_CONFIRMED.getWaitingAmount()
            );
            return;
        }
        organizationStatisticRepository.updateOrganizationStatisticCounts(
                feedback.getOrganizationIdValue(),
                DELETED_WAITING.getTotalAmount(),
                DELETED_WAITING.getConfirmedAmount(),
                DELETED_WAITING.getWaitingAmount()
        );
    }

    public AdminFeedbackListResponse getFeedbackPage(
            final UUID organizationUuid,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackSortType sortBy
    ) {
        if (!organizationRepository.existsOrganizationByUuid(organizationUuid)) {
            throw new ResourceNotFoundException("해당 ID(id = " + organizationUuid + ")인 단체를 찾을 수 없습니다.");
        }
        final Pageable pageable = Pageable.ofSize(size + 1);

        final FeedbackSortStrategy feedbackSortStrategy = feedbackSortStrategyFactory.find(sortBy);
        final List<FeedbackItem> feedbacks = feedbackSortStrategy.getSortedFeedbacks(organizationUuid, status, cursorId,
                pageable);

        final FeedbackPage feedbackPage = FeedbackPage.createCursorPage(feedbacks, size);
        return AdminFeedbackListResponse.from(feedbackPage);
    }

    @Transactional
    @BusinessActionLog
    public UpdateFeedbackCommentResponse updateFeedbackComment(
            final Long adminId,
            final UpdateFeedbackCommentRequest request,
            final Long feedbackId
    ) {
        validateAuthentication(adminId, feedbackId);
        final Feedback feedback = getFeedback(feedbackId);
        feedback.updateCommentAndStatus(request.toComment());

        organizationStatisticRepository.updateOrganizationStatisticCounts(
                feedback.getOrganizationIdValue(),
                WAITING_TO_CONFIRMED.getTotalAmount(),
                WAITING_TO_CONFIRMED.getConfirmedAmount(),
                WAITING_TO_CONFIRMED.getWaitingAmount()
        );
        return UpdateFeedbackCommentResponse.from(feedback);
    }

    private Feedback getFeedback(final Long feedbackId) {
        return feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + feedbackId + ")인 피드백을 찾을 수 없습니다."));
    }

    private void validateAuthentication(final Long adminId, final Long feedbackId) {
        if (!adminRepository.existsFeedbackId(adminId, feedbackId)) {
            throw new ForbiddenException("admin" + adminId + "는 해당 요청에 대한 권한이 없습니다.");
        }
    }

    @Transactional
    public void deleteAllByOrganizationIds(final List<Long> organizationIds) {
        feedBackRepository.deleteAllByOrganizationIdIn(organizationIds);
    }

    @Transactional
    public void deleteByOrganizationId(final Long organizationId) {
        feedBackRepository.deleteAllByOrganizationId(organizationId);
    }

    public ClustersResponse getTopClusters(final UUID organizationUuid, final int limit) {
        if (!organizationRepository.existsOrganizationByUuid(organizationUuid)) {
            throw new ResourceNotFoundException("해당 organizationUuid(uuid = " + organizationUuid + ")로 찾을 수 없습니다.");
        }
        final List<ClusterInfo> clusterInfos = feedBackRepository.findTopClusters(organizationUuid,
                PageRequest.of(0, limit));
        return ClustersResponse.from(clusterInfos);
    }

    public ClusterFeedbacksResponse getFeedbacksByClusterId(final Long clusterId) {
        final EmbeddingCluster embeddingCluster = embeddingClusterRepository.findById(clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 clusterid(id = " + clusterId + ")로 찾을 수 없습니다."));
        final List<FeedbackEmbeddingCluster> feedbackEmbeddingClusters = feedbackEmbeddingClusterRepository.findAllByEmbeddingCluster(
                embeddingCluster);
        return ClusterFeedbacksResponse.of(feedbackEmbeddingClusters, embeddingCluster.getLabel());
    }

    public String createDownloadJob(final UUID organizationUuid) {
        if (!organizationRepository.existsOrganizationByUuid(organizationUuid)) {
            throw new ResourceNotFoundException("해당 ID(id = " + organizationUuid + ")인 단체를 찾을 수 없습니다.");
        }

        final FeedbackDownloadJob job = FeedbackDownloadJob.create(organizationUuid.toString());
        feedbackDownloadJobStore.save(job);

        feedbackFileDownloadService.createAndUploadFileAsync(job.getJobId(), organizationUuid);

        return job.getJobId();
    }

    public FeedbackDownloadJob getDownloadJobStatus(final String jobId) {
        final FeedbackDownloadJob job = feedbackDownloadJobStore.getById(jobId);
        if (job == null) {
            throw new ResourceNotFoundException("해당 ID(id = " + jobId + ")인 작업을 찾을 수 없습니다.");
        }
        return job;
    }

    public String getDownloadUrl(final String jobId) {
        final FeedbackDownloadJob job = feedbackDownloadJobStore.getById(jobId);
        if (job == null) {
            throw new ResourceNotFoundException("해당 ID(id = " + jobId + ")인 작업을 찾을 수 없습니다.");
        }

        if (job.getStatus() != DownloadStatus.COMPLETED) {
            throw new DownloadJobNotCompletedException("파일 생성이 완료되지 않았습니다. 현재 상태: " + job.getStatus());
        }

        if (job.getDownloadUrl() == null) {
            throw new DownloadUrlNotGeneratedException("다운로드 URL이 생성되지 않았습니다.");
        }

        final String filename = generateDownloadFileName();
        return s3PresignedDownloadService.generateDownloadUrlFromImageUrl(job.getDownloadUrl(), filename);
    }

    private String generateDownloadFileName() {
        final LocalDateTime now = LocalDateTime.now();
        final String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("feedback_export_%s.xlsx", timestamp);
    }
}
