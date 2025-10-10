package feedzupzup.backend.feedback.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.auth.presentation.annotation.LoginOrganizer;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.response.ClusterFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackStatisticResponse;
import feedzupzup.backend.feedback.dto.response.ClusterRepresentativeFeedbacksResponse;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organizer.dto.LoginOrganizerInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Admin Feedback", description = "피드백 API(관리자 권한)")
@SecurityRequirement(name = "SessionAuth")
public interface AdminFeedbackApi {

    @Operation(summary = "관리자용 피드백 목록 조회", description = "특정 단체의 피드백 목록을 조회합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/organizations/{organizationUuid}/feedbacks")
    SuccessResponse<AdminFeedbackListResponse> getAdminFeedbacks(
            @Parameter(hidden = true) @LoginOrganizer final LoginOrganizerInfo loginOrganizerInfo,
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organizationUuid") final UUID organizationUuid,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") final int size,
            @Parameter(description = "커서 ID") @RequestParam(required = false) final Long cursorId,
            @Parameter(description = "게시글 상태") @RequestParam(required = false) final ProcessStatus status,
            @Parameter(description = "정렬 기준", example = "LATEST, OLDEST, LIKES") @RequestParam(defaultValue = "LATEST") final FeedbackSortType sortBy
    );

    @Operation(summary = "피드백 삭제", description = "피드백을 삭제합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/admin/feedbacks/{feedbackId}")
    SuccessResponse<Void> delete(
            @Parameter(hidden = true) @AdminAuthenticationPrincipal final AdminSession adminSession,
            @Parameter(description = "피드백 ID", example = "1") @PathVariable("feedbackId") final Long feedbackId
    );

    @Operation(summary = "답글 추가", description = "답글을 추가합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답글 추가 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/admin/feedbacks/{feedbackId}/comment")
    SuccessResponse<UpdateFeedbackCommentResponse> updateFeedbackComment(
            @Parameter(hidden = true) @AdminAuthenticationPrincipal final AdminSession adminSession,
            @Parameter(description = "피드백 ID", example = "1") @PathVariable("feedbackId") final Long feedbackId,
            @RequestBody @Valid final UpdateFeedbackCommentRequest request
    );

    @Operation(summary = "피드백 전체 처리 현황 조회", description = "피드백의 전체 처리 현황을 조회합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/feedbacks/statistics")
    SuccessResponse<FeedbackStatisticResponse> getAllFeedbackStatistics(
            @Parameter(hidden = true) @AdminAuthenticationPrincipal final AdminSession adminSession
    );

    @Operation(summary = "모든 클러스터 대표 피드백 전체 조회", description = "각 클러스터의 대표 피드백을 조회합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/organizations/{organizationUuid}/clusters")
    SuccessResponse<ClusterRepresentativeFeedbacksResponse> getRepresentativeCluster(
            @Parameter(hidden = true) @LoginOrganizer final AdminSession adminSession,
            @PathVariable("organizationUuid") UUID organizationUuid
    );

    @Operation(summary = "특정 클러스터 피드백 전체 조회", description = "특정 클러스터에 속한 전체 피드백을 조회합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/organizations/clusters/{clusterId}")
    SuccessResponse<ClusterFeedbacksResponse> getFeedbacksByClusterId(
            @PathVariable("clusterId") UUID clusterId
    );
}
