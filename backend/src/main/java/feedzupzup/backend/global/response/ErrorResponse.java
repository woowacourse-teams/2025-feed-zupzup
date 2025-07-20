package feedzupzup.backend.global.response;

public record ErrorResponse<T>(
        T data,
        int status,
        String code,
        String message
) {

    public static <T> ErrorResponse<T> error(T data, ErrorCode errorCode, String message) {
        return new ErrorResponse<>(data, errorCode.getHttpStatus().value(), errorCode.getCode(), message);
    }

    public static ErrorResponse<Void> error(ErrorCode errorCode, String message) {
        return error(null, errorCode, message);
    }
}
