package feedzupzup.backend.feedback.infrastructure.excel;

import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.CATEGORY;
import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.COMMENT;
import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.CONTENT;
import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.FEEDBACK_NUMBER;
import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.IMAGE;
import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.IS_SECRET;
import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.LIKE_COUNT;
import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.POSTED_AT;
import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.STATUS;
import static feedzupzup.backend.feedback.infrastructure.excel.FeedbackExcelColumn.USER_NAME;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackExcelExporter;
import feedzupzup.backend.global.exception.InfrastructureException.PoiExcelExportException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.s3.service.S3DownloadService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackExcelExporterPoi implements FeedbackExcelExporter {

    private final S3DownloadService s3DownloadService;

    @Override
    public void export(
            final Organization organization,
            final List<Feedback> feedbacks,
            final HttpServletResponse response
    ) {
        final int windowSize = 10;
        try (final SXSSFWorkbook workbook = new SXSSFWorkbook(windowSize)) {
            final String sheetName = organization.getName().getValue();
            final Sheet sheet = workbook.createSheet(sheetName);

            addFeedbackHeaderRow(sheet);
            addFeedbackDataRows(sheet, workbook, feedbacks);

            streamExcelResponse(response, workbook);
        } catch (IOException e) {
            throw new PoiExcelExportException("엑셀 파일 생성 중 오류가 발생했습니다.");
        }
    }

    private void addFeedbackHeaderRow(final Sheet sheet) {
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
            sheet.autoSizeColumn(column.columnIndex());
        }
    }

    private void addFeedbackDataRows(
            final Sheet sheet,
            final SXSSFWorkbook workbook,
            final List<Feedback> feedbacks
    ) {
        int rowNum = 1;
        int feedbackNum = 1;
        for (final Feedback feedback : feedbacks) {
            final Row row = sheet.createRow(rowNum);

            row.createCell(FEEDBACK_NUMBER.columnIndex()).setCellValue(feedbackNum++);
            row.createCell(CONTENT.columnIndex()).setCellValue(feedback.getContent().getValue());
            row.createCell(CATEGORY.columnIndex())
                    .setCellValue(feedback.getOrganizationCategory().getCategory().getKoreanName());

            addImageCell(sheet, workbook, row, feedback, rowNum);

            row.createCell(LIKE_COUNT.columnIndex()).setCellValue(feedback.getLikeCountValue());
            row.createCell(IS_SECRET.columnIndex()).setCellValue(feedback.isSecret() ? "Y" : "N");
            row.createCell(STATUS.columnIndex()).setCellValue(feedback.getStatus().name());
            row.createCell(COMMENT.columnIndex())
                    .setCellValue(feedback.getComment().getValue() == null ? "" : feedback.getComment().getValue());
            row.createCell(USER_NAME.columnIndex()).setCellValue(feedback.getUserName().getValue());
            row.createCell(POSTED_AT.columnIndex()).setCellValue(feedback.getPostedAt().getValue());

            rowNum++;
        }
    }

    private void addImageCell(
            final Sheet sheet,
            final SXSSFWorkbook workbook,
            final Row row,
            final Feedback feedback,
            final int rowNum
    ) {
        if (feedback.getImageUrl() == null) {
            row.createCell(IMAGE.columnIndex()).setCellValue("");
            return;
        }

        try {
            final String imageUrl = feedback.getImageUrl().getValue();
            final byte[] imageData = s3DownloadService.downloadFile(imageUrl);

            final int pictureIndex = workbook.addPicture(imageData, Workbook.PICTURE_TYPE_JPEG);

            final ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
            anchor.setCol1(IMAGE.columnIndex());
            anchor.setRow1(rowNum);

            final Picture picture = sheet.createDrawingPatriarch().createPicture(anchor, pictureIndex);
            picture.resize(1, 1);
        } catch (Exception e) {
            row.createCell(IMAGE.columnIndex()).setCellValue("");
            log.error("엑셀 파일 생성을 위한 이미지 다운로드 실패");
        }
    }

    private void streamExcelResponse(
            final HttpServletResponse response,
            final SXSSFWorkbook workbook
    ) {
        final String fileName = generateFileName();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Transfer-Encoding", "chunked");

        try (final OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new PoiExcelExportException("엑셀 파일 스트리밍 중 오류가 발생했습니다.");
        }
    }

    private String generateFileName() {
        final LocalDateTime now = LocalDateTime.now();
        final String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("feedback_export_%s.xlsx", timestamp);
    }
}
