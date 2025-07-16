package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.feedback.api.UserFeedbackApi;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.dto.response.UnLikeResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserFeedbackController implements UserFeedbackApi {

    @Override
    public SuccessResponse<UserFeedbackListResponse> getUserFeedbacks(Long placeId, int size, Long cursorId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SuccessResponse<CreateFeedbackResponse> create(Long placeId, CreateFeedbackRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SuccessResponse<LikeResponse> like(Long feedbackId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SuccessResponse<UnLikeResponse> unlike(Long feedbackId) {
        throw new UnsupportedOperationException();
    }
}
