package feedzupzup.backend.feedback.infrastructure.excel;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.s3.service.S3DownloadService;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FeedbackImageProducer {

    private final S3DownloadService s3DownloadService;
    private final BlockingQueue<FeedbackWithImage> queue;
    private final ExecutorService executor;

    public CompletableFuture<Void> produceImages(final List<Feedback> feedbacks) {
        return CompletableFuture.runAsync(() -> {
            final List<CompletableFuture<Void>> downloadJobs = feedbacks.stream()
                    .map(this::downloadJob)
                    .toList();

            CompletableFuture.allOf(downloadJobs.toArray(CompletableFuture[]::new)).join();

            notifyFinished();
        }, executor);
    }

    private CompletableFuture<Void> downloadJob(final Feedback feedback) {
        return CompletableFuture
                .supplyAsync(() -> downloadImage(feedback), executor)
                .thenAccept(imageResult -> enqueue(feedback, imageResult))
                .exceptionally(ex -> {
                    log.error("이미지 다운로드 실패", ex);
                    enqueue(feedback, ImageDownloadResult.failed());
                    return null;
                });
    }

    private void enqueue(final Feedback feedback, final ImageDownloadResult imageResult) {
        try {
            queue.put(new FeedbackWithImage(feedback, imageResult));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException("Queue 추가 실패", e);
        }
    }

    private ImageDownloadResult downloadImage(final Feedback feedback) {
        if (feedback.getImageUrl() == null) {
            return ImageDownloadResult.noImage();
        }

        try {
            final String imageUrl = feedback.getImageUrl().getValue();
            final byte[] imageData = s3DownloadService.downloadFile(imageUrl);
            return ImageDownloadResult.success(imageData);
        } catch (Exception e) {
            log.error("이미지 다운로드 실패: {}", feedback.getImageUrl(), e);
            return ImageDownloadResult.failed();
        }
    }

    private void notifyFinished() {
        try {
            queue.put(FeedbackWithImage.POISON_PILL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException("종료 신호 전송 실패", e);
        }
    }
}
