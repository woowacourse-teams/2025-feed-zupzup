package feedzupzup.backend.global.response;

public record ErrorResponse<T>(
        int status,
        String code,
        String message
) {

    public static <T> ErrorResponse<T> error(ErrorCode errorCode) {
        return new ErrorResponse<>(errorCode.getHttpStatus().value(), errorCode.getCode(), errorCode.getMessage());
    }
}
