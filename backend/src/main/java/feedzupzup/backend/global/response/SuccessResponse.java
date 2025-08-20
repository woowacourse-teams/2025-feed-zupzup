package feedzupzup.backend.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "성공 응답")
public record SuccessResponse<T>(
        @Schema(description = "응답 데이터")
        T data,
        @Schema(description = "상태 코드", example = "200")
        int status,
        @Schema(description = "상태 메시지", example = "OK")
        String message
) {

    public static <T> SuccessResponse<T> success(final HttpStatus httpStatus, final T data) {
        return new SuccessResponse<>(data, httpStatus.value(), httpStatus.name());
    }

    public static SuccessResponse<Void> success(final HttpStatus httpStatus) {
        return success(httpStatus, null);
    }
}
