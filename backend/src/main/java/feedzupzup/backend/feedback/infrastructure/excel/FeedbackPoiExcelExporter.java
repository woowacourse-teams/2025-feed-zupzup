package feedzupzup.backend.feedback.infrastructure.excel;

import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.CATEGORY;
import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.COMMENT;
import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.CONTENT;
import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.FEEDBACK_NUMBER;
import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.IMAGE;
import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.IS_SECRET;
import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.LIKE_COUNT;
import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.POSTED_AT;
import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.STATUS;
import static feedzupzup.backend.feedback.domain.FeedbackExcelColumn.USER_NAME;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackExcelColumn;
import feedzupzup.backend.feedback.domain.FeedbackExcelExporter;
import feedzupzup.backend.global.exception.InfrastructureException.PoiExcelExportException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.s3.service.S3DownloadService;
import java.io.IOException;
import java.io.OutputStream;
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
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedbackPoiExcelExporter implements FeedbackExcelExporter {

    private final S3DownloadService s3DownloadService;

    @Override
    public void export(
            final Organization organization,
            final List<Feedback> feedbacks,
            final OutputStream outputStream
    ) {
        final int windowSize = 10;
        try (final SXSSFWorkbook workbook = new SXSSFWorkbook(windowSize)) {
            final String sheetName = organization.getName().getValue();
            final SXSSFSheet sheet = workbook.createSheet(sheetName);

            addFeedbackHeaderRow(sheet);
            addFeedbackDataRows(sheet, workbook, feedbacks);

            workbook.write(outputStream);
            outputStream.flush();
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

            int columnWidth = 4000;
            sheet.setColumnWidth(column.columnIndex(), columnWidth);
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
                    .setCellValue(feedback.getComment() == null ? "" : feedback.getComment().getValue());
            row.createCell(USER_NAME.columnIndex()).setCellValue(feedback.getUserName().getValue());
            row.createCell(POSTED_AT.columnIndex()).setCellValue(feedback.getPostedAt().getValue().toString());

            rowNum++;
        }
    }

    /**
     * s3에서 이미지를 다운받아 엑셀의 Cell에 삽입한다.
     * <p>
     * 참고 - 이미지 다운이 실패하더라도 엑셀 다운로드는 성공시키기 위해 예외를 던지지 않음
     */
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
            anchor.setCol2(IMAGE.columnIndex() + 1);
            anchor.setRow2(rowNum + 1);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

            final Picture picture = sheet.createDrawingPatriarch().createPicture(anchor, pictureIndex);
            picture.resize(1, 1);
        } catch (Exception e) {
            row.createCell(IMAGE.columnIndex()).setCellValue("이미지 로드 실패");
            log.error("엑셀 파일 생성을 위한 이미지 다운로드 실패", e);
        }
    }
}
