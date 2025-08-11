package feedzupzup.backend.feedback.api;

import feedzupzup.backend.feedback.application.FeedbackOrderBy;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.dto.response.StatisticResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import java.util.List;
import feedzupzup.backend.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "User Feedback", description = "피드백 API(사용자 권한)")
public interface UserFeedbackApi {

    @Operation(summary = "피드백 목록 조회", description = "특정 장소의 피드백 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/organizations/{organizationId}/feedbacks")
    SuccessResponse<UserFeedbackListResponse> getUserFeedbacks(
            @Parameter(description = "장소 ID", example = "1") @PathVariable("organizationId") final Long organizationId,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") final int size,
            @Parameter(description = "커서 ID") @RequestParam(required = false) final Long cursorId,
            @Parameter(description = "게시글 상태") @RequestParam(required = false) final ProcessStatus status,
            @Parameter(description = "정렬 기준", example = "LATEST, OLDEST, LIKES") @RequestParam(defaultValue = "LATEST") final FeedbackOrderBy orderBy
    );

    @Operation(summary = "피드백 생성", description = "새로운 피드백을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/organizations/{organizationId}/feedbacks")
    SuccessResponse<CreateFeedbackResponse> create(
            @Parameter(description = "장소 ID", example = "1") @PathVariable("organizationId") final Long organizationId,
            @RequestBody @Valid final CreateFeedbackRequest request
    );

    @Operation(summary = "피드백 좋아요", description = "피드백에 좋아요를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict")
    })
    @PostMapping("/feedbacks/{feedbackId}/like")
    SuccessResponse<LikeResponse> like(
            @Parameter(description = "피드백 ID", example = "1") @PathVariable("feedbackId") final Long feedbackId
    );

    @Operation(summary = "피드백 좋아요 취소", description = "피드백의 좋아요를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict")
    })
    @DeleteMapping("/feedbacks/{feedbackId}/like")
    SuccessResponse<LikeResponse> unlike(
            @Parameter(description = "피드백 ID", example = "1") @PathVariable("feedbackId") final Long feedbackId
    );

    @Operation(summary = "피드백 통계 계산", description = "피드백의 통계를 계산합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @GetMapping("/organizations/{organizationId}/statistic")
    SuccessResponse<StatisticResponse> getStatistic(
            @Parameter(description = "조직 ID", example = "1") @PathVariable("organizationId") final Long organizationId
    );

    @Operation(summary = "내가 쓴 피드백 목록 조회", description = "내가 쓴 피드백 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/organizations/{organizationId}/feedbacks/my")
    SuccessResponse<MyFeedbackListResponse> getMyFeedbacks(
            @Parameter(description = "장소 ID", example = "1") @PathVariable("organizationId") final Long organizationId,
            @Parameter(description = "정렬 기준", example = "LATEST, OLDEST, LIKES") @RequestParam(defaultValue = "LATEST") final FeedbackOrderBy orderBy,
            @Parameter(description = "내가 쓴 피드백 ID 목록") @RequestParam final List<Long> feedbackIds
    );
}
