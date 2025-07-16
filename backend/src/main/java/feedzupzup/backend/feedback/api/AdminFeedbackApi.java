package feedzupzup.backend.feedback.api;

import feedzupzup.backend.feedback.dto.UpdateFeedStatusResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackSecretResponse;

@Tag(name = "Admin Feedback", description = "피드백 API(관리자 권한)")
public interface AdminFeedbackApi {

    @Operation(summary = "관리자용 피드백 목록 조회", description = "특정 장소의 피드백 목록을 조회합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AdminFeedbackListResponse.class))),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    })
    @GetMapping("/admin/places/{placeId}/feedbacks")
    SuccessResponse<AdminFeedbackListResponse> getAdminFeedbacks(
            @Parameter(description = "장소 ID", example = "1") @PathVariable("placeId") Long placeId,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") final int size,
            @Parameter(description = "커서 ID", example = "1") @RequestParam(required = false) final Long cursorId
    );

    @Operation(summary = "피드백 비밀 상태 변경", description = "피드백의 비밀 상태를 변경합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공",
                    content = @Content(schema = @Schema(implementation = UpdateFeedbackSecretResponse.class))),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @PatchMapping("/admin/feedbacks/{feedbackId}/secret")
    SuccessResponse<UpdateFeedbackSecretResponse> updateFeedbackSecret(
            @Parameter(description = "피드백 ID", example = "1") @PathVariable("feedbackId") final Long feedbackId,
            @RequestBody @Valid final UpdateFeedbackSecretRequest request
    );

    @Operation(summary = "피드백 삭제", description = "피드백을 삭제합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @DeleteMapping("/admin/feedbacks/{feedbackId}")
    SuccessResponse<Void> deleteFeedback(
            @Parameter(description = "피드백 ID", example = "1") @PathVariable("feedbackId") final Long feedbackId
    );

    @Operation(summary = "피드백 처리 상태 변경", description = "피드백의 처리 상태를 변경합니다. (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    })
    @PatchMapping("/admin/feedbacks/{feedbackId}/status")
    SuccessResponse<UpdateFeedStatusResponse> updateFeedbackStatus(
            @Parameter(description = "피드백 ID", example = "1") @PathVariable("feedbackId") final Long feedbackId,
            @RequestBody @Valid final UpdateFeedbackStatusRequest request
    );
}
