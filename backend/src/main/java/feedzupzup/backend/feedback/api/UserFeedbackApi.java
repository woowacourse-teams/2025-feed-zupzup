package feedzupzup.backend.feedback.api;

import feedzupzup.backend.auth.presentation.annotation.Visitor;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.StatisticResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.global.util.CookieUtilization;
import feedzupzup.backend.guest.domain.guest.Guest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "User Feedback", description = "피드백 API(사용자 권한)")
public interface UserFeedbackApi {

    @Operation(summary = "피드백 목록 조회", description = "특정 단체의 피드백 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/organizations/{organizationUuid}/feedbacks")
    SuccessResponse<UserFeedbackListResponse> getUserFeedbacks(
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organizationUuid") final UUID organizationUuid,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") final int size,
            @Parameter(description = "커서 ID") @RequestParam(required = false) final Long cursorId,
            @Parameter(description = "게시글 상태") @RequestParam(required = false) final ProcessStatus status,
            @Parameter(description = "정렬 기준", example = "LATEST, OLDEST, LIKES") @RequestParam(defaultValue = "LATEST") final FeedbackSortType sortBy
    );

    @Operation(summary = "피드백 생성", description = "새로운 피드백을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/organizations/{organizationUuid}/feedbacks")
    SuccessResponse<CreateFeedbackResponse> create(
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organizationUuid") final UUID organizationUuid,
            @RequestBody @Valid final CreateFeedbackRequest request
    );

    @Operation(summary = "피드백 좋아요", description = "피드백에 좋아요를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/feedbacks/{feedbackId}/like")
    SuccessResponse<LikeResponse> like(
            final HttpServletResponse response,
            @Parameter(description = "피드백 ID", example = "1") @PathVariable("feedbackId") final Long feedbackId,
            @Parameter(hidden = true) @Visitor Guest guest
    );

    @Operation(summary = "피드백 좋아요 취소", description = "피드백의 좋아요를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/feedbacks/{feedbackId}/unlike")
    SuccessResponse<LikeResponse> unlike(
            final HttpServletResponse response,
            @Parameter(description = "피드백 ID", example = "1") @PathVariable("feedbackId") final Long feedbackId,
            @Parameter(hidden = true) @Visitor Guest guest
    );

    @Operation(summary = "피드백 통계 계산", description = "피드백의 통계를 계산합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/organizations/{organizationUuid}/statistic")
    SuccessResponse<StatisticResponse> getStatistic(
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organizationUuid") final UUID organizationUuid
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
