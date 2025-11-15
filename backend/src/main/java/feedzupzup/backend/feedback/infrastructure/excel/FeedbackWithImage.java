package feedzupzup.backend.feedback.infrastructure.excel;

import feedzupzup.backend.feedback.domain.Feedback;

record FeedbackWithImage(Feedback feedback, ImageDownloadResult imageResult, boolean isPoisonPill) {

    static final FeedbackWithImage POISON_PILL = new FeedbackWithImage(null, null, true);

    FeedbackWithImage(final Feedback feedback, final ImageDownloadResult imageResult) {
        this(feedback, imageResult, false);
    }
}
