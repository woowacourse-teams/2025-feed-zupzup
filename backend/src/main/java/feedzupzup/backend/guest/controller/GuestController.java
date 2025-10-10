package feedzupzup.backend.guest.controller;

import feedzupzup.backend.auth.presentation.annotation.Guest;
import feedzupzup.backend.guest.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.guest.api.GuestApi;
import feedzupzup.backend.guest.application.GuestService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GuestController implements GuestApi {

    private final GuestService guestService;

    @Override
    public SuccessResponse<MyFeedbackListResponse> getMyFeedbacks(
            final UUID organizationUuid,
            @Guest final feedzupzup.backend.guest.domain.guest.Guest guest
    ) {
        final MyFeedbackListResponse response = guestService.getMyFeedbackPage(
                organizationUuid, guest);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<LikeHistoryResponse> getMyLikeHistories(
            final UUID organizationUuid,
            @Guest final feedzupzup.backend.guest.domain.guest.Guest guest
    ) {
        final LikeHistoryResponse response = guestService.findGuestLikeHistories(
                organizationUuid, guest);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
