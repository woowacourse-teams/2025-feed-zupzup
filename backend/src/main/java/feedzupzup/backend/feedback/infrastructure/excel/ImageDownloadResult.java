package feedzupzup.backend.feedback.infrastructure.excel;

record ImageDownloadResult(byte[] imageData, ResultType type) {

    enum ResultType {
        SUCCESS, FAILED, NO_IMAGE
    }

    static ImageDownloadResult success(final byte[] imageData) {
        return new ImageDownloadResult(imageData, ResultType.SUCCESS);
    }

    static ImageDownloadResult failed() {
        return new ImageDownloadResult(null, ResultType.FAILED);
    }

    static ImageDownloadResult noImage() {
        return new ImageDownloadResult(null, ResultType.NO_IMAGE);
    }

    boolean isFailed() {
        return type == ResultType.FAILED;
    }

    boolean isNoImage() {
        return type == ResultType.NO_IMAGE;
    }
}
