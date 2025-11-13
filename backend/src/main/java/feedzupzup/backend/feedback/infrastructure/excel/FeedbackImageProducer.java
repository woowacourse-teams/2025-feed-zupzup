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

    /**
     * 비동기로 이미지를 다운로드하고 BlockingQueue에 추가
     *
     * @param feedbacks 다운로드할 피드백 목록
     * @return 비동기 작업 완료를 나타내는 CompletableFuture
     */
    public CompletableFuture<Void> startAsyncDownload(final List<Feedback> feedbacks) {
        return CompletableFuture.runAsync(() -> {
            final List<CompletableFuture<Void>> downloadFutures = feedbacks.stream()
                    .map(this::downloadAndEnqueue)
                    .toList();

            // 모든 다운로드 완료 대기
            CompletableFuture.allOf(downloadFutures.toArray(new CompletableFuture[0]))
                    .join();

            try {
                queue.put(FeedbackWithImage.POISON_PILL);
                log.debug("다운로드 완료 신호 전송");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CompletionException("종료 신호 전송 실패", e);
            }
        }, executor);
    }

    private CompletableFuture<Void> downloadAndEnqueue(final Feedback feedback) {
        return CompletableFuture
                .supplyAsync(() -> downloadImage(feedback), executor)
                .thenAccept(imageResult -> {
                    try {
                        final FeedbackWithImage item = new FeedbackWithImage(
                                feedback, imageResult
                        );

                        // BlockingQueue에 추가
                        // 가득 차면 여기서 블로킹됨 (자동 배압!)
                        queue.put(item);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("Queue 추가 중 인터럽트 발생", e);
                        throw new CompletionException("Queue 추가 실패", e);
                    }
                })
                .exceptionally(ex -> {
                    log.error("이미지 다운로드 및 enqueue 실패: {}",
                            feedback.getImageUrl(), ex);

                    // 실패해도 Queue에 추가 (이미지만 실패 처리)
                    try {
                        queue.put(new FeedbackWithImage(
                                feedback,
                                ImageDownloadResult.failed(ex.getMessage())
                        ));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("실패 항목 Queue 추가 중 인터럽트", e);
                    }
                    return null;
                });
    }

    /**
     * 단일 피드백의 이미지 다운로드
     * <p>
     * - S3DownloadService의 동기 메서드 호출 - CompletableFuture의 executor 스레드에서 실행됨
     */
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
            return ImageDownloadResult.failed(e.getMessage());
        }
    }
}
