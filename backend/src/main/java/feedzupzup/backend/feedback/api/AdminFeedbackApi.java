package feedzupzup.backend.feedback.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.auth.presentation.annotation.LoginOrganizer;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortType;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.ClusterFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.ClustersResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackDownloadJobResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackDownloadJobStatusResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackStatisticResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.global.response.ErrorResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.organizer.dto.LoginOrganizerInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Operation(summary = "모든 클러스터 대표 피드백 전체 조회", description = "각 클러스터의 대표 피드백을 조회합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/organizations/{organizationUuid}/clusters")
    SuccessResponse<ClustersResponse> getTopClusters(
            @Parameter(hidden = true) @LoginOrganizer final LoginOrganizerInfo loginOrganizerInfo,
            @PathVariable("organizationUuid") UUID organizationUuid,
            @RequestParam(required = false, defaultValue = "5") final int limit
    );

    @Operation(summary = "특정 클러스터 피드백 전체 조회", description = "특정 클러스터에 속한 전체 피드백을 조회합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/organizations/{organizationUuid}/clusters/{clusterId}")
    SuccessResponse<ClusterFeedbacksResponse> getFeedbacksByClusterId(
            @Parameter(hidden = true) @LoginOrganizer final LoginOrganizerInfo loginOrganizerInfo,
            @PathVariable("clusterId") Long clusterId
    );

    @Operation(
            summary = "파일 생성 작업 시작 요청",
            description = "피드백 다운로드 파일 생성 작업을 시작합니다. (관리자 전용)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "작업 생성 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/admin/organizations/{organizationUuid}/download-jobs")
    SuccessResponse<FeedbackDownloadJobResponse> createDownloadJob(
            @Parameter(hidden = true) @LoginOrganizer final LoginOrganizerInfo loginOrganizerInfo,
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organizationUuid") final UUID organizationUuid
    );

    @Operation(
            summary = "파일 생성 상태(진행도) 조회",
            description = "피드백 다운로드 파일 생성 작업의 진행 상태를 조회합니다. (Polling API)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(
                    responseCode = "500",
                    description = "작업 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/organizations/{organizationUuid}/download-jobs/{jobId}/status")
    SuccessResponse<FeedbackDownloadJobStatusResponse> getDownloadJobStatus(
            @Parameter(hidden = true) @LoginOrganizer final LoginOrganizerInfo loginOrganizerInfo,
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organizationUuid") final UUID organizationUuid,
            @Parameter(description = "작업 ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("jobId") final String jobId
    );

    @Operation(
            summary = "파일 다운로드 요청",
            description = "생성된 피드백 파일의 다운로드 URL로 리다이렉션합니다. (관리자 전용)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "302",
                    description = "S3 Presigned URL로 리다이렉션",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(
                    responseCode = "400",
                    description = "파일 생성이 완료되지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "SessionAuth")
    @GetMapping("/admin/organizations/{organizationUuid}/download-jobs/{jobId}")
    void downloadFile(
            @Parameter(hidden = true) @LoginOrganizer final LoginOrganizerInfo loginOrganizerInfo,
            @Parameter(description = "단체 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organizationUuid") final UUID organizationUuid,
            @Parameter(description = "작업 ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("jobId") final String jobId,
            @Parameter(hidden = true) final HttpServletResponse response
    );
}
