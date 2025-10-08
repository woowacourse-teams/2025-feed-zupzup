package feedzupzup.backend.guest.controller;

import feedzupzup.backend.auth.presentation.annotation.Visitor;
import feedzupzup.backend.feedback.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.guest.api.GuestApi;
import feedzupzup.backend.guest.application.GuestService;
import feedzupzup.backend.guest.domain.guest.Guest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GuestController implements GuestApi {

    private final GuestService guestService;

    @Override
    public SuccessResponse<MyFeedbackListResponse> getMyFeedbacks(
            final UUID organizationUuid,
            @Visitor final Guest guest
    ) {
        final MyFeedbackListResponse response = guestService.getMyFeedbackPage(
                organizationUuid, guest);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<LikeHistoryResponse> getMyLikeHistories(
            final HttpServletResponse response, final Guest guest) {
        return null;
    }
}
