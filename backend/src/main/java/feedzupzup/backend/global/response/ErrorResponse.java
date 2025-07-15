package feedzupzup.backend.global.response;

public record ErrorResponse<T> (
        T data,
        String code,
        String message
){

    public static <T> ErrorResponse<T> error(T data, String code, String message) {
        return new ErrorResponse<>(data, code, message);
    }

    public static ErrorResponse<Void> error(String code, String message) {
        return error(null, code, message);
    }
}
