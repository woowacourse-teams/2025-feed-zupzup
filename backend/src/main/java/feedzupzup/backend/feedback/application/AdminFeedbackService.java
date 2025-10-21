package feedzupzup.backend.feedback.application;

import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.feedback.domain.ClusterRepresentativeFeedback;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackAmount;
import feedzupzup.backend.feedback.domain.FeedbackPage;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.service.sort.FeedbackSortStrategy;
import feedzupzup.backend.feedback.domain.service.sort.FeedbackSortStrategyFactory;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.ClusterFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.ClusterRepresentativeFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import feedzupzup.backend.feedback.dto.response.FeedbackStatisticResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.log.BusinessActionLog;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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

    @Transactional
    @BusinessActionLog
    public void delete(
            final Long adminId,
            final Long feedbackId
    ) {
        validateAuthentication(adminId, feedbackId);
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

        FeedbackSortStrategy feedbackSortStrategy = feedbackSortStrategyFactory.find(sortBy);
        List<FeedbackItem> feedbacks = feedbackSortStrategy.getSortedFeedbacks(organizationUuid, status, cursorId,
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
        hasAccessToFeedback(adminId, feedbackId);

        final Feedback feedback = getFeedback(feedbackId);

        validateAuthentication(adminId, feedbackId);

        feedback.updateCommentAndStatus(request.toComment());

        return UpdateFeedbackCommentResponse.from(feedback);
    }

    private void hasAccessToFeedback(final Long adminId, final Long feedbackId) {
        if (!adminRepository.existsFeedbackId(adminId, feedbackId)) {
            throw new ForbiddenException("admin" + adminId + "는 해당 요청에 대한 권한이 없습니다.");
        }
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

    public FeedbackStatisticResponse calculateFeedbackStatistics(final Long adminId) {
        final FeedbackAmount feedbackAmount = feedBackRepository.findFeedbackStatisticsByAdminId(adminId);

        final long totalCount = feedbackAmount.totalCount();
        final long confirmedCount = feedbackAmount.confirmedCount();
        final int reflectionRate = feedbackAmount.calculateReflectionRate();

        return new FeedbackStatisticResponse(confirmedCount, totalCount, reflectionRate);
    }

    @Transactional
    public void deleteAllByOrganizationIds(final List<Long> organizationIds) {
        feedBackRepository.deleteAllByOrganizationIdIn(organizationIds);
    }

    @Transactional
    public void deleteByOrganizationId(final Long organizationId) {
        feedBackRepository.deleteAllByOrganizationId(organizationId);
    }

    public ClusterRepresentativeFeedbacksResponse getRepresentativeCluster(final Long adminId, final UUID organizationUuid) {
        List<ClusterRepresentativeFeedback> clusterRepresentativeFeedbacks = feedBackRepository.findAllRepresentativeFeedbackPerCluster(
                organizationUuid);
        return ClusterRepresentativeFeedbacksResponse.from(clusterRepresentativeFeedbacks);
    }

    public ClusterFeedbacksResponse getFeedbacksByClusterId(final UUID clusterId) {
        List<Feedback> feedbacks = feedBackRepository.findAllByClustering_ClusterId(clusterId);
        if (feedbacks.isEmpty()) {
            throw new ResourceNotFoundException("해당 클러스터 ID(clusterID = " + clusterId + ")를 가진 피드백은 존재하지 않습니다.");
        }
        return ClusterFeedbacksResponse.of(feedbacks);
    }
}
