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
import feedzupzup.backend.global.exception.InfrastructureException.PoiExcelExportException;
import java.util.concurrent.BlockingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

@RequiredArgsConstructor
@Slf4j
public class FeedbackImageConsumer {

    private final Sheet sheet;
    private final BlockingQueue<FeedbackWithImage> queue;
    private final SXSSFWorkbook workbook;

    /**
     * Consumer: Queue에서 꺼내서 엑셀에 추가
     * <p>
     * - 메인 스레드에서 실행 - Queue가 비어있으면 대기 (블로킹) - POISON_PILL 받으면 종료
     *
     * @param totalCount 전체 피드백 개수 (진행률 로깅용)
     */
    public void consumeAndAddToExcel(final int totalCount) {
        int rowNum = 1;
        int feedbackNum = 1;
        int processedCount = 0;

        try {
            while (true) {
                // Queue에서 꺼내기 (비어있으면 대기)
                final FeedbackWithImage item = queue.take();

                // 종료 신호 확인
                if (item.isPoisonPill()) {
                    log.debug("종료 신호 수신, 엑셀 작성 완료");
                    break;
                }

                final Feedback feedback = item.getFeedback();
                final ImageDownloadResult imageResult = item.getImageResult();
                final Row row = sheet.createRow(rowNum);

                // 엑셀 행에 데이터 추가
                addFeedbackToRow(row, feedback, imageResult, feedbackNum, rowNum);

                rowNum++;
                feedbackNum++;
                processedCount++;

                // 진행률 로깅 (20개마다)
                if (processedCount % 20 == 0 || processedCount == totalCount) {
                    final int progress = (processedCount * 100) / totalCount;
                    log.info("엑셀 작성 진행: {}/{} ({}%)",
                            processedCount, totalCount, progress);
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("엑셀 작성 중 인터럽트 발생", e);
            throw new PoiExcelExportException("엑셀 파일 생성이 중단되었습니다.");
        }
    }

    /**
     * Row에 피드백 데이터 추가
     */
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

        addImageCellWithDownloadedData(row, imageResult, rowNum);

        row.createCell(LIKE_COUNT.columnIndex()).setCellValue(feedback.getLikeCountValue());
        row.createCell(IS_SECRET.columnIndex()).setCellValue(feedback.isSecret() ? "Y" : "N");
        row.createCell(STATUS.columnIndex()).setCellValue(feedback.getStatus().name());
        row.createCell(COMMENT.columnIndex())
                .setCellValue(feedback.getComment() == null ? "" : feedback.getComment().getValue());
        row.createCell(USER_NAME.columnIndex()).setCellValue(feedback.getUserName().getValue());
        row.createCell(POSTED_AT.columnIndex()).setCellValue(feedback.getPostedAt().getValue().toString());
    }

    /**
     * 이미 다운로드된 이미지 데이터를 엑셀 셀에 추가
     * <p>
     * - 다운로드 완료된 바이너리 데이터를 엑셀에 삽입 - 실패한 경우 "이미지 로드 실패" 텍스트 표시
     */
    private void addImageCellWithDownloadedData(
            final Row row,
            final ImageDownloadResult imageResult,
            final int rowNum
    ) {
        if (imageResult.isNoImage()) {
            row.createCell(IMAGE.columnIndex()).setCellValue("");
            return;
        }

        if (imageResult.isFailed()) {
            row.createCell(IMAGE.columnIndex()).setCellValue("이미지 로드 실패");
            return;
        }

        try {
            final byte[] imageData = imageResult.getImageData();
            final int pictureIndex = workbook.addPicture(imageData, Workbook.PICTURE_TYPE_JPEG);

            final ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
            anchor.setCol1(IMAGE.columnIndex());
            anchor.setRow1(rowNum);
            anchor.setCol2(IMAGE.columnIndex() + 1);
            anchor.setRow2(rowNum + 1);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

            final Picture picture = sheet.createDrawingPatriarch()
                    .createPicture(anchor, pictureIndex);
            picture.resize(1, 1);
        } catch (Exception e) {
            row.createCell(IMAGE.columnIndex()).setCellValue("이미지 삽입 실패");
            log.error("엑셀에 이미지 삽입 실패", e);
        }
    }
}
