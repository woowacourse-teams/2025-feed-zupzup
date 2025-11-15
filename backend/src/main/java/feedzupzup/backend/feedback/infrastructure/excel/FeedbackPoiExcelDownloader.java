package feedzupzup.backend.feedback.infrastructure.excel;

import feedzupzup.backend.feedback.domain.Feedback;
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
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedbackPoiExcelDownloader implements FeedbackExcelExporter {

    private static final int QUEUE_CAPACITY = 15;
    private static final int PRODUCER_THREAD = 1;
    private static final int DOWNLOAD_THREADS = 10;

    private final S3DownloadService s3DownloadService;

    @Override
    public void download(
            final Organization organization,
            final List<Feedback> feedbacks,
            final OutputStream outputStream
    ) {
        log.info("피드백 엑셀 다운로드 시작: 조직={}, 피드백 개수={}", organization.getName().getValue(), feedbacks.size());

        final int windowSize = 10;
        final ExecutorService executor = Executors.newFixedThreadPool(PRODUCER_THREAD + DOWNLOAD_THREADS);

        try (final SXSSFWorkbook workbook = new SXSSFWorkbook(windowSize)) {
            final String sheetName = organization.getName().getValue();
            final Sheet sheet = workbook.createSheet(sheetName);

            createHeaderRow(sheet);
            createDataRows(sheet, workbook, feedbacks, executor);

            workbook.write(outputStream);
            outputStream.flush();

            log.info("피드백 엑셀 다운로드 완료");
        } catch (IOException e) {
            throw new PoiExcelExportException("엑셀 파일 생성 중 오류가 발생했습니다.");
        } finally {
            shutdownExecutor(executor);
        }
    }

    private void createHeaderRow(final Sheet sheet) {
        final CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        final Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final Row headerRow = sheet.createRow(0);

        for (final FeedbackExcelColumn column : FeedbackExcelColumn.values()) {
            final Cell cell = headerRow.createCell(column.columnIndex());
            cell.setCellValue(column.headerName());
            cell.setCellStyle(headerStyle);

            final int columnWidth = column.columnWidth();
            sheet.setColumnWidth(column.columnIndex(), columnWidth);
        }
    }

    private void createDataRows(
            final Sheet sheet,
            final SXSSFWorkbook workbook,
            final List<Feedback> feedbacks,
            final ExecutorService executor
    ) {
        final BlockingQueue<FeedbackWithImage> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

        final FeedbackImageProducer producer = new FeedbackImageProducer(s3DownloadService, queue, executor);
        final CompletableFuture<Void> produceJob = producer.produceImages(feedbacks);

        final FeedbackRowWriter consumer = new FeedbackRowWriter(sheet, queue, workbook);
        consumer.consumeToExcel(feedbacks.size());

        try {
            produceJob.join();
        } catch (CompletionException e) {
            log.error("이미지 다운로드 작업 중 오류 발생", e);
            throw new PoiExcelExportException("이미지 다운로드 중 오류가 발생했습니다.", e);
        }
    }

    private void shutdownExecutor(final ExecutorService executor) {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("Executor 강제 종료 실패");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
