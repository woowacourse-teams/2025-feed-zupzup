package feedzupzup.backend.feedback.infrastructure.excel;

import feedzupzup.backend.feedback.domain.Feedback;

/**
 * 피드백과 다운로드된 이미지를 함께 담는 DTO
 * <p>
 * BlockingQueue를 통해 다운로드 스레드 → 메인 스레드로 전달
 */
public class FeedbackWithImage {

    private final Feedback feedback;
    private final ImageDownloadResult imageResult;
    private final boolean isPoisonPill;

    /**
     * POISON_PILL: 종료 신호용 특수 객체
     * <p>
     * 모든 다운로드 완료 후 Queue에 추가하여 Consumer에게 종료 알림
     */
    static final FeedbackWithImage POISON_PILL = new FeedbackWithImage(null, null, true);

    FeedbackWithImage(Feedback feedback, ImageDownloadResult imageResult) {
        this(feedback, imageResult, false);
    }

    private FeedbackWithImage(Feedback feedback, ImageDownloadResult imageResult, boolean isPoisonPill) {
        this.feedback = feedback;
        this.imageResult = imageResult;
        this.isPoisonPill = isPoisonPill;
    }

    Feedback getFeedback() {
        return feedback;
    }

    ImageDownloadResult getImageResult() {
        return imageResult;
    }

    boolean isPoisonPill() {
        return isPoisonPill;
    }
}
