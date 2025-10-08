package feedzupzup.backend.guest.api;

import feedzupzup.backend.auth.presentation.annotation.Visitor;
import feedzupzup.backend.feedback.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.guest.domain.guest.Guest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "게스트 API", description = "게스트 API(비회원)")
public interface GuestApi {

    @Operation(summary = "내가 쓴 피드백 목록 조회", description = "내가 쓴 피드백 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/organizations/{organizationUuid}/feedbacks/my")
    SuccessResponse<MyFeedbackListResponse> getMyFeedbacks(
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organizationUuid") final UUID organizationUuid,
            @Parameter(hidden = true) @Visitor Guest guest
    );

    @Operation(summary = "좋아요 목록 조회", description = "본인이 누른 좋아요 목록들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/feedbacks/my-likes")
    SuccessResponse<LikeHistoryResponse> getMyLikeHistories(
            final HttpServletResponse response,
            @Parameter(hidden = true) @Visitor Guest guest
    );

}
