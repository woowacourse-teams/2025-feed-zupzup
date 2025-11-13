package feedzupzup.backend.feedback.infrastructure.excel;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackExcelColumn;
import feedzupzup.backend.feedback.domain.FeedbackExcelExporter;
import feedzupzup.backend.global.exception.InfrastructureException.PoiExcelExportException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.s3.service.S3DownloadService;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class FeedbackPoiExcelExporter implements FeedbackExcelExporter {

    private static final int QUEUE_CAPACITY = 20;
    private static final int DOWNLOAD_THREADS = 10;

    private final S3DownloadService s3DownloadService;

    @Override
    public void export(
            final Organization organization,
            final List<Feedback> feedbacks,
            final OutputStream outputStream
    ) {
        log.info("피드백 엑셀 다운로드 시작: 조직={}, 피드백 개수={}", organization.getName().getValue(), feedbacks.size());

        final int windowSize = 10;
        final ExecutorService executor = Executors.newFixedThreadPool(DOWNLOAD_THREADS);

        try (final SXSSFWorkbook workbook = new SXSSFWorkbook(windowSize)) {
            final String sheetName = organization.getName().getValue();
            final SXSSFSheet sheet = workbook.createSheet(sheetName);

            addHeaderRow(sheet);
            addDataRows(sheet, workbook, feedbacks, executor);

            workbook.write(outputStream);
            outputStream.flush();

            log.info("피드백 엑셀 다운로드 완료: {}개", feedbacks.size());
        } catch (IOException e) {
            log.error("엑셀 파일 생성 중 오류 발생", e);
            throw new PoiExcelExportException("엑셀 파일 생성 중 오류가 발생했습니다.");
        } finally {
            shutdownExecutor(executor);
        }
    }

    private void addHeaderRow(final Sheet sheet) {
        final Workbook workbook = sheet.getWorkbook();

        final CellStyle headerStyle = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final int startRowNum = 0;
        final Row headerRow = sheet.createRow(startRowNum);

        for (final FeedbackExcelColumn column : FeedbackExcelColumn.values()) {
            final Cell cell = headerRow.createCell(column.columnIndex());
            cell.setCellValue(column.headerName());
            cell.setCellStyle(headerStyle);

            int columnWidth = 4000;
            sheet.setColumnWidth(column.columnIndex(), columnWidth);
        }
    }

    private void addDataRows(
            final Sheet sheet,
            final SXSSFWorkbook workbook,
            final List<Feedback> feedbacks,
            final ExecutorService executor
    ) {
        final BlockingQueue<FeedbackWithImage> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

        // Producer: 백그라운드에서 비동기 다운로드
        final FeedbackImageProducer producer = new FeedbackImageProducer(
                s3DownloadService, queue, executor
        );
        final CompletableFuture<Void> producerTask = producer.startAsyncDownload(feedbacks);

        // Consumer: 메인 스레드에서 엑셀에 추가
        final FeedbackImageConsumer consumer = new FeedbackImageConsumer(
                sheet, queue, workbook
        );
        consumer.consumeAndAddToExcel(feedbacks.size());

        // 다운로드 완료 대기
        try {
            producerTask.join();
        } catch (CompletionException e) {
            log.error("이미지 다운로드 작업 중 오류 발생", e);
            throw new PoiExcelExportException("이미지 다운로드 중 오류가 발생했습니다.");
        }
    }

    /**
     * ExecutorService 안전하게 종료
     */
    private void shutdownExecutor(final ExecutorService executor) {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("Executor가 30초 내에 종료되지 않아 강제 종료 시도");
                executor.shutdownNow();

                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("Executor 강제 종료 실패");
                }
            }
        } catch (InterruptedException e) {
            log.error("Executor 종료 중 인터럽트 발생", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
