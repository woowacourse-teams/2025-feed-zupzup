package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.feedback.api.AdminFeedbackApi;
import feedzupzup.backend.feedback.dto.UpdateFeedStatusResponse;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackSecretResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AdminFeedbackController implements AdminFeedbackApi {

    @Override
    public SuccessResponse<AdminFeedbackListResponse> getAdminFeedbacks(Long placeId, int size, Long cursorId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SuccessResponse<UpdateFeedbackSecretResponse> updateFeedbackSecret(Long feedbackId,
            UpdateFeedbackSecretRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SuccessResponse<Void> deleteFeedback(Long feedbackId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SuccessResponse<UpdateFeedStatusResponse> updateFeedbackStatus(Long feedbackId,
            UpdateFeedbackStatusRequest request) {
        throw new UnsupportedOperationException();
    }
}
