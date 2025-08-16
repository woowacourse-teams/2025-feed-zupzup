package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.feedback.api.AdminFeedbackApi;
import feedzupzup.backend.feedback.application.AdminFeedbackService;
import feedzupzup.backend.feedback.domain.vo.FeedbackOrderBy;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackSecretResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackStatusResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminFeedbackController implements AdminFeedbackApi {

    private final AdminFeedbackService adminFeedbackService;

    @Override
    public SuccessResponse<AdminFeedbackListResponse> getAdminFeedbacks(
            final UUID organizationUuid,
            final int size,
            final Long cursorId,
            final ProcessStatus status,
            final FeedbackOrderBy orderBy
    ) {
        return SuccessResponse.success(HttpStatus.OK, adminFeedbackService.getFeedbackPage(
                organizationUuid,
                size,
                cursorId,
                status,
                orderBy
        ));
    }

    @Override
    public SuccessResponse<UpdateFeedbackSecretResponse> updateFeedbackSecret(
            final Long feedbackId,
            final UpdateFeedbackSecretRequest request
    ) {
        UpdateFeedbackSecretResponse response = adminFeedbackService.updateFeedbackSecret(feedbackId, request);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<Void> delete(final Long feedbackId) {
        adminFeedbackService.delete(feedbackId);
        return SuccessResponse.success(HttpStatus.OK);
    }

    @Override
    public SuccessResponse<UpdateFeedbackStatusResponse> updateFeedbackStatus(
            final Long feedbackId,
            final UpdateFeedbackStatusRequest request
    ) {
        final UpdateFeedbackStatusResponse response = adminFeedbackService.updateFeedbackStatus(
                request, feedbackId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<UpdateFeedbackCommentResponse> updateFeedbackComment(
            final Long feedbackId,
            final UpdateFeedbackCommentRequest request
    ) {
        final UpdateFeedbackCommentResponse response = adminFeedbackService.updateFeedbackComment(
                request, feedbackId);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
