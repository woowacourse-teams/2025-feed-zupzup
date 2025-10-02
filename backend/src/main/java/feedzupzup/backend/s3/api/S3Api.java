package feedzupzup.backend.s3.api;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.s3.dto.request.PresignedUrlRequest;
import feedzupzup.backend.s3.dto.response.PresignedUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "S3", description = "S3 API")
public interface S3Api {

    @Operation(summary = "presignedUrl 발급", description = "이미지 업로드를 위한 S3 presignedUrl을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발급 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/presigned-url")
    SuccessResponse<PresignedUrlResponse> generatePresignedUrl(
            @RequestBody @Valid final PresignedUrlRequest request
    );
}
