package feedzupzup.backend.feedback.infrastructure.excel;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.InfrastructureException.PoiExcelExportException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.s3.service.S3DownloadService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackPoiExcelExporterTest {

    @Mock
    private S3DownloadService s3DownloadService;

    @InjectMocks
    private FeedbackPoiExcelExporter feedbackPoiExcelExporter;

    @Test
    @DisplayName("엑셀 파일이 정상적으로 생성된다")
    void export_Success() throws IOException {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        final OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization, category);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization, category);

        final List<Feedback> feedbacks = List.of(feedback1, feedback2);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        feedbackPoiExcelExporter.export(organization, feedbacks, outputStream);

        // then
        assertThat(outputStream.size()).isGreaterThan(0);

        // 엑셀 파일 내용 검증
        try (final Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            final Sheet sheet = workbook.getSheetAt(0);

            // 시트 이름 검증
            assertThat(sheet.getSheetName()).isEqualTo(organization.getName().getValue());

            // 헤더 행 검증
            final Row headerRow = sheet.getRow(0);
            assertThat(headerRow.getCell(FEEDBACK_NUMBER.columnIndex()).getStringCellValue()).isEqualTo(
                    FEEDBACK_NUMBER.headerName());
            assertThat(headerRow.getCell(CONTENT.columnIndex()).getStringCellValue()).isEqualTo(CONTENT.headerName());
            assertThat(headerRow.getCell(CATEGORY.columnIndex()).getStringCellValue()).isEqualTo(CATEGORY.headerName());
            assertThat(headerRow.getCell(IMAGE.columnIndex()).getStringCellValue()).isEqualTo(IMAGE.headerName());
            assertThat(headerRow.getCell(LIKE_COUNT.columnIndex()).getStringCellValue()).isEqualTo(
                    LIKE_COUNT.headerName());
            assertThat(headerRow.getCell(IS_SECRET.columnIndex()).getStringCellValue()).isEqualTo(
                    IS_SECRET.headerName());
            assertThat(headerRow.getCell(STATUS.columnIndex()).getStringCellValue()).isEqualTo(STATUS.headerName());
            assertThat(headerRow.getCell(COMMENT.columnIndex()).getStringCellValue()).isEqualTo(COMMENT.headerName());
            assertThat(headerRow.getCell(USER_NAME.columnIndex()).getStringCellValue()).isEqualTo(
                    USER_NAME.headerName());
            assertThat(headerRow.getCell(POSTED_AT.columnIndex()).getStringCellValue()).isEqualTo(
                    POSTED_AT.headerName());

            // 첫 번째 피드백 데이터 검증
            final Row dataRow1 = sheet.getRow(1);
            assertThat(dataRow1.getCell(FEEDBACK_NUMBER.columnIndex()).getNumericCellValue()).isEqualTo(1);
            assertThat(dataRow1.getCell(CONTENT.columnIndex()).getStringCellValue()).isEqualTo("장소별 피드백");
            assertThat(dataRow1.getCell(CATEGORY.columnIndex()).getStringCellValue()).isEqualTo("건의");
            assertThat(dataRow1.getCell(IS_SECRET.columnIndex()).getStringCellValue()).isEqualTo("N");
            assertThat(dataRow1.getCell(USER_NAME.columnIndex()).getStringCellValue()).isEqualTo("테스트유저");

            // 두 번째 피드백 데이터 검증
            final Row dataRow2 = sheet.getRow(2);
            assertThat(dataRow2.getCell(FEEDBACK_NUMBER.columnIndex()).getNumericCellValue()).isEqualTo(2);
            assertThat(dataRow2.getCell(CONTENT.columnIndex()).getStringCellValue()).isEqualTo("장소별 피드백");
            assertThat(dataRow2.getCell(CATEGORY.columnIndex()).getStringCellValue()).isEqualTo("건의");
        }
    }

    @Test
    @DisplayName("OutputStream이 닫혀있으면 예외가 발생한다")
    void export_ClosedOutputStream_ThrowsException() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        final OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization, category);
        final List<Feedback> feedbacks = List.of(feedback);

        final OutputStream closedOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Stream is closed");
            }
        };

        // when & then
        assertThatThrownBy(() -> feedbackPoiExcelExporter.export(organization, feedbacks, closedOutputStream))
                .isInstanceOf(PoiExcelExportException.class)
                .hasMessageContaining("엑셀 파일 생성 중 오류가 발생했습니다");
    }

    @Test
    @DisplayName("엑셀의 피드백 열 번호가 순차적으로 증가한다")
    void export_FeedbackNumberIncrementsSequentially() throws IOException {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        final OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization, category);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization, category);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(organization, category);

        final List<Feedback> feedbacks = List.of(feedback1, feedback2, feedback3);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        feedbackPoiExcelExporter.export(organization, feedbacks, outputStream);

        // then
        try (final Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            final Sheet sheet = workbook.getSheetAt(0);

            assertThat(sheet.getRow(1).getCell(FEEDBACK_NUMBER.columnIndex()).getNumericCellValue()).isEqualTo(1);
            assertThat(sheet.getRow(2).getCell(FEEDBACK_NUMBER.columnIndex()).getNumericCellValue()).isEqualTo(2);
            assertThat(sheet.getRow(3).getCell(FEEDBACK_NUMBER.columnIndex()).getNumericCellValue()).isEqualTo(3);
        }
    }

    @Test
    @DisplayName("이미지 다운로드 실패 시 '이미지 로드 실패'로 표시되고 엑셀 생성은 계속된다")
    void export_ImageDownloadFails_ContinuesWithEmptyCell() throws IOException {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        final OrganizationCategory category = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        final Feedback feedback = FeedbackFixture.createFeedbackWithImageUrl(
                organization, category, "https://test.s3.amazonaws.com/test.jpg");

        final List<Feedback> feedbacks = List.of(feedback);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Mock S3 이미지 다운로드 실패
        given(s3DownloadService.downloadFile(anyString())).willThrow(new RuntimeException("S3 다운로드 실패"));

        // when
        feedbackPoiExcelExporter.export(organization, feedbacks, outputStream);

        // then - 엑셀은 정상적으로 생성되어야 함
        assertThat(outputStream.size()).isGreaterThan(0);

        try (final Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()))) {
            final Sheet sheet = workbook.getSheetAt(0);
            final Row dataRow = sheet.getRow(1);

            // 이미지 셀이 빈 문자열로 처리되었는지 확인
            assertThat(dataRow.getCell(IMAGE.columnIndex()).getStringCellValue()).isEqualTo("이미지 로드 실패");
        }
    }
}
