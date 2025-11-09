package feedzupzup.backend.feedback.application;

import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.feedback.domain.ClusterInfo;
import feedzupzup.backend.feedback.domain.EmbeddingCluster;
import feedzupzup.backend.feedback.domain.EmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackExcelExporter;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.service.sort.FeedbackSortStrategy;
import feedzupzup.backend.feedback.domain.service.sort.FeedbackSortStrategyFactory;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.ClusterFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.ClustersResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.log.BusinessActionLog;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.domain.OrganizationStatistic;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final FeedbackExcelExporter feedbackExcelExporter;

    @Transactional
    @BusinessActionLog
    public void delete(
            final Long adminId,
            final Long feedbackId
    ) {
        validateAuthentication(adminId, feedbackId);
        final Feedback feedback = getFeedback(feedbackId);
        final OrganizationStatistic organizationStatistic = organizationStatisticRepository.findByOrganizationId(
                feedback.getOrganization().getId());

        if (feedback.getStatus() == ProcessStatus.CONFIRMED) {
            organizationStatistic.decreaseConfirmedCount();
        }
        organizationStatistic.decreaseWaitingCount();

        feedbackEmbeddingClusterRepository.deleteByFeedback_Id(feedbackId);
        feedBackRepository.deleteById(feedbackId);
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

        final OrganizationStatistic organizationStatistic = organizationStatisticRepository.findByOrganizationId(
                feedback.getOrganization().getId());

        organizationStatistic.increaseConfirmedCount();
        organizationStatistic.decreaseWaitingCount();

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

    public void downloadFeedbacks(final UUID organizationUuid, final OutputStream outputStream) {
        final Organization organization = organizationRepository.findByUuid(organizationUuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 ID(id = " + organizationUuid + ")인 단체를 찾을 수 없습니다."));

        final List<Feedback> feedbacks = feedBackRepository.findByOrganization(organization);

        feedbackExcelExporter.export(organization, feedbacks, outputStream);
    }

    public String generateExportFileName() {
        final LocalDateTime now = LocalDateTime.now();
        final String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("feedback_export_%s.xlsx", timestamp);
    }
}
