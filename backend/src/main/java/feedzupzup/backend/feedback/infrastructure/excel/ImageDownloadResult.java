package feedzupzup.backend.feedback.infrastructure.excel;

/**
 * 이미지 다운로드 결과를 담는 DTO
 * <p>
 * 3가지 상태: - SUCCESS: 다운로드 성공 - FAILED: 다운로드 실패 - NO_IMAGE: 이미지 없음 (URL이 null)
 */
public class ImageDownloadResult {

    private final byte[] imageData;
    private final String errorMessage;
    private final ResultType type;

    private enum ResultType {
        SUCCESS, FAILED, NO_IMAGE
    }

    private ImageDownloadResult(byte[] imageData, String errorMessage, ResultType type) {
        this.imageData = imageData;
        this.errorMessage = errorMessage;
        this.type = type;
    }

    static ImageDownloadResult success(byte[] imageData) {
        return new ImageDownloadResult(imageData, null, ResultType.SUCCESS);
    }

    static ImageDownloadResult failed(String errorMessage) {
        return new ImageDownloadResult(null, errorMessage, ResultType.FAILED);
    }

    static ImageDownloadResult noImage() {
        return new ImageDownloadResult(null, null, ResultType.NO_IMAGE);
    }

    boolean isFailed() {
        return type == ResultType.FAILED;
    }

    boolean isNoImage() {
        return type == ResultType.NO_IMAGE;
    }

    byte[] getImageData() {
        return imageData;
    }
}
