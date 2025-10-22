package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.feedback.api.AdminFeedbackApi;
import feedzupzup.backend.feedback.application.AdminFeedbackService;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.ClusterFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackStatisticResponse;
import feedzupzup.backend.feedback.dto.response.ClustersResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organizer.dto.LoginOrganizerInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminFeedbackController implements AdminFeedbackApi {

    private final AdminFeedbackService adminFeedbackService;

    @Override
    public SuccessResponse<AdminFeedbackListResponse> getAdminFeedbacks(
            final LoginOrganizerInfo loginOrganizerInfo,
            final UUID organizationUuid,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackSortType sortBy
    ) {
        return SuccessResponse.success(HttpStatus.OK, adminFeedbackService.getFeedbackPage(
                loginOrganizerInfo.organizationUuid(),
                size,
                cursorId,
                status,
                sortBy
        ));
    }

    @Override
    public SuccessResponse<Void> delete(
            final AdminSession adminSession,
            final Long feedbackId
    ) {
        adminFeedbackService.delete(adminSession.adminId(), feedbackId);
        return SuccessResponse.success(HttpStatus.OK);
    }

    @Override
    public SuccessResponse<UpdateFeedbackCommentResponse> updateFeedbackComment(
            final AdminSession adminSession,
            final Long feedbackId,
            final UpdateFeedbackCommentRequest request
    ) {
        final UpdateFeedbackCommentResponse response = adminFeedbackService.updateFeedbackComment(
                adminSession.adminId(), request, feedbackId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<FeedbackStatisticResponse> getAllFeedbackStatistics(
            final AdminSession adminSession
    ) {
        final FeedbackStatisticResponse response = adminFeedbackService.calculateFeedbackStatistics(
                adminSession.adminId());
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<ClustersResponse> getTopClusters(
            final LoginOrganizerInfo loginOrganizerInfo,
            final UUID organizationUuid,
            final int limit
    ) {
        ClustersResponse response = adminFeedbackService.getTopClusters(organizationUuid, limit);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<ClusterFeedbacksResponse> getFeedbacksByClusterId(
            final LoginOrganizerInfo loginOrganizerInfo,
            final Long clusterId
    ) {
        ClusterFeedbacksResponse response = adminFeedbackService.getFeedbacksByClusterId(clusterId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
