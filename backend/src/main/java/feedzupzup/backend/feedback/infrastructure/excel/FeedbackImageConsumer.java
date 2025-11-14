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
import feedzupzup.backend.global.exception.InfrastructureException.PoiExcelExportException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

@RequiredArgsConstructor
@Slf4j
public class FeedbackImageConsumer {

    private static final short DEFAULT_ROW_HEIGHT = 300;

    private final Sheet sheet;
    private final BlockingQueue<FeedbackWithImage> queue;
    private final SXSSFWorkbook workbook;

    void consumeToExcel(final int totalCount) {
        int rowNum = 1;
        int feedbackNum = 1;
        int processedCount = 0;

        try {
            while (true) {
                final FeedbackWithImage item = queue.take();

                if (item.isPoisonPill()) {
                    break;
                }

                final Row row = sheet.createRow(rowNum);
                addFeedbackToRow(row, item.feedback(), item.imageResult(), feedbackNum, rowNum);

                rowNum++;
                feedbackNum++;
                processedCount++;

                if (processedCount % 10 == 0 || processedCount == totalCount) {
                    log.info("엑셀 작성 진행: {}/{}", processedCount, totalCount);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PoiExcelExportException("엑셀 파일 생성이 중단되었습니다.");
        }
    }

    private void addFeedbackToRow(
            final Row row,
            final Feedback feedback,
            final ImageDownloadResult imageResult,
            final int feedbackNum,
            final int rowNum
    ) {
        row.createCell(FEEDBACK_NUMBER.columnIndex()).setCellValue(feedbackNum);
        row.createCell(CONTENT.columnIndex()).setCellValue(feedback.getContent().getValue());
        row.createCell(CATEGORY.columnIndex())
                .setCellValue(feedback.getOrganizationCategory().getCategory().getKoreanName());

        addImageCell(row, imageResult, rowNum);

        row.createCell(LIKE_COUNT.columnIndex()).setCellValue(feedback.getLikeCountValue());
        row.createCell(IS_SECRET.columnIndex()).setCellValue(feedback.isSecret() ? "Y" : "N");
        row.createCell(STATUS.columnIndex()).setCellValue(feedback.getStatus().name());
        row.createCell(COMMENT.columnIndex())
                .setCellValue(feedback.getComment() == null ? "" : feedback.getComment().getValue());
        row.createCell(USER_NAME.columnIndex()).setCellValue(feedback.getUserName().getValue());
        addDateCell(row, feedback.getPostedAt().getValue());
    }

    private void addImageCell(
            final Row row,
            final ImageDownloadResult imageResult,
            final int rowNum
    ) {
        if (imageResult.isNoImage()) {
            setEmptyImageCell(row);
            return;
        }

        if (imageResult.isFailed()) {
            setFailedImageCell(row, "이미지 로드 실패");
            return;
        }

        try {
            sheet.setColumnWidth(IMAGE.columnIndex(), IMAGE.columnWidth());
            adjustRowHeightByImageRatio(row, imageResult.imageData());
            insertImageToPicture(imageResult.imageData(), rowNum);
        } catch (IOException e) {
            setFailedImageCell(row, "이미지 로드 실패");
            log.error("이미지 로드 실패", e);
        } catch (Exception e) {
            setFailedImageCell(row, "이미지 삽입 실패");
            log.error("엑셀에 이미지 삽입 실패", e);
        }
    }

    private void setEmptyImageCell(final Row row) {
        row.createCell(IMAGE.columnIndex()).setCellValue("");
        row.setHeight(DEFAULT_ROW_HEIGHT);
    }

    private void setFailedImageCell(final Row row, final String message) {
        row.createCell(IMAGE.columnIndex()).setCellValue(message);
        row.setHeight(DEFAULT_ROW_HEIGHT);
    }

    private void adjustRowHeightByImageRatio(final Row row, final byte[] imageData) throws IOException {
        final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
        final int imageWidth = bufferedImage.getWidth();
        final int imageHeight = bufferedImage.getHeight();

        final double aspectRatio = (double) imageHeight / imageWidth;
        final int pixelWidth = (IMAGE.columnWidth() / 256) * 7;
        final short rowHeight = (short) (pixelWidth * aspectRatio * 20);

        row.setHeight(rowHeight);
    }

    private void insertImageToPicture(final byte[] imageData, final int rowNum) {
        final int pictureIndex = workbook.addPicture(imageData, Workbook.PICTURE_TYPE_JPEG);

        final ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
        anchor.setCol1(IMAGE.columnIndex());
        anchor.setRow1(rowNum);
        anchor.setCol2(IMAGE.columnIndex() + 1);
        anchor.setRow2(rowNum + 1);
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

        final Picture picture = sheet.createDrawingPatriarch().createPicture(anchor, pictureIndex);
        picture.resize(1.0, 1.0);
    }

    private void addDateCell(final Row row, final LocalDateTime dateTime) {
        final Cell cell = row.createCell(POSTED_AT.columnIndex());
        final Date date = Date.from(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
        cell.setCellValue(date);

        final CellStyle dateStyle = workbook.createCellStyle();
        final CreationHelper creationHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        cell.setCellStyle(dateStyle);
    }
}
