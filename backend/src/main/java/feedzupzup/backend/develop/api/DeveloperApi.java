package feedzupzup.backend.develop.api;


import feedzupzup.backend.develop.dto.UpdateAdminPasswordRequest;
import feedzupzup.backend.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Developer", description = "개발자 API")
public interface DeveloperApi {

    @Operation(summary = "관리자 비밀번호 변경", description = "관리자의 비밀번호 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한 없음", useReturnTypeSchema = true)
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/develop/change-password")
    SuccessResponse<Void> change(
            @RequestBody final UpdateAdminPasswordRequest request
    );

    @Operation(summary = "기존 피드백 클러스터링", description = "기존 피드백 클러스터링 배치 작업")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "기존 피드백 클러스터링 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한 없음", useReturnTypeSchema = true)
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/develop/feedbacks/clustering/batch")
    SuccessResponse<Void> batchClustering(@RequestParam final String secret);
}
