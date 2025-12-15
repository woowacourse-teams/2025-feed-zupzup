package feedzupzup.backend.feedback.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackDownloadJobStore;
import feedzupzup.backend.feedback.domain.FeedbackExcelDownloader;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.s3.service.S3UploadService;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackFileDownloadServiceUnitTest {

    @InjectMocks
    private FeedbackFileDownloadService feedbackFileDownloadService;

    @Mock
    private FeedbackDownloadJobStore feedbackDownloadJobStore;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private FeedbackRepository feedBackRepository;

    @Mock
    private FeedbackExcelDownloader feedbackExcelDownloader;

    @Mock
    private S3UploadService s3UploadService;

    @Mock
    private Organization organization;

    @Mock
    private Feedback feedback1;

    @Mock
    private Feedback feedback2;

    @Mock
    private FeedbackDownloadJob feedbackDownloadJob;

    @Test
    @DisplayName("작업을 찾을 수 없을 때 로그만 남기고 종료한다")
    void createAndUploadFileAsync_jobNotFound_returnsEarly() {
        // given
        String jobId = UUID.randomUUID().toString();
        UUID organizationUuid = UUID.randomUUID();

        given(feedbackDownloadJobStore.getById(jobId)).willReturn(null);

        // when
        feedbackFileDownloadService.createAndUploadFileAsync(jobId, organizationUuid);

        // then
        verify(feedbackDownloadJobStore).getById(jobId);
        verify(organizationRepository, never()).findByUuid(any());
    }

    @Test
    @DisplayName("정상적으로 파일을 생성하고 업로드한다")
    void createAndUploadFileAsync_success() throws Exception {
        // given
        String jobId = UUID.randomUUID().toString();
        UUID organizationUuid = UUID.randomUUID();
        String s3Url = "https://s3.amazonaws.com/test-bucket/feedback_file.xlsx";
        List<Feedback> feedbacks = Arrays.asList(feedback1, feedback2);

        given(feedbackDownloadJobStore.getById(jobId)).willReturn(feedbackDownloadJob);
        given(organizationRepository.findByUuid(organizationUuid)).willReturn(Optional.of(organization));
        given(feedBackRepository.findByOrganization(organization)).willReturn(feedbacks);
        given(s3UploadService.uploadFile(eq("xlsx"), eq("feedback_file"), eq(jobId), any(byte[].class)))
                .willReturn(s3Url);

        // when
        feedbackFileDownloadService.createAndUploadFileAsync(jobId, organizationUuid);

        // then
        verify(feedbackDownloadJobStore).getById(jobId);
        verify(organizationRepository).findByUuid(organizationUuid);
        verify(feedBackRepository).findByOrganization(organization);
        verify(feedbackExcelDownloader).download(eq(organization), eq(feedbacks), any(ByteArrayOutputStream.class), eq(jobId));
        verify(s3UploadService).uploadFile(eq("xlsx"), eq("feedback_file"), eq(jobId), any(byte[].class));
        verify(feedbackDownloadJob).completeWithUrl(s3Url);
    }

    @Test
    @DisplayName("피드백이 없어도 정상적으로 파일을 생성하고 업로드한다")
    void createAndUploadFileAsync_noFeedbacks_success() throws Exception {
        // given
        String jobId = UUID.randomUUID().toString();
        UUID organizationUuid = UUID.randomUUID();
        String s3Url = "https://s3.amazonaws.com/test-bucket/feedback_file.xlsx";
        List<Feedback> emptyFeedbacks = Collections.emptyList();

        given(feedbackDownloadJobStore.getById(jobId)).willReturn(feedbackDownloadJob);
        given(organizationRepository.findByUuid(organizationUuid)).willReturn(Optional.of(organization));
        given(feedBackRepository.findByOrganization(organization)).willReturn(emptyFeedbacks);
        given(s3UploadService.uploadFile(eq("xlsx"), eq("feedback_file"), eq(jobId), any(byte[].class)))
                .willReturn(s3Url);

        // when
        feedbackFileDownloadService.createAndUploadFileAsync(jobId, organizationUuid);

        // then
        verify(feedbackDownloadJobStore).getById(jobId);
        verify(organizationRepository).findByUuid(organizationUuid);
        verify(feedBackRepository).findByOrganization(organization);
        verify(feedbackExcelDownloader).download(eq(organization), eq(emptyFeedbacks), any(ByteArrayOutputStream.class), eq(jobId));
        verify(s3UploadService).uploadFile(eq("xlsx"), eq("feedback_file"), eq(jobId), any(byte[].class));
        verify(feedbackDownloadJob).completeWithUrl(s3Url);
    }

    @Test
    @DisplayName("Organization을 찾을 수 없을 때 작업을 실패 처리한다")
    void createAndUploadFileAsync_organizationNotFound_failsJob() {
        // given
        String jobId = UUID.randomUUID().toString();
        UUID organizationUuid = UUID.randomUUID();

        given(feedbackDownloadJobStore.getById(jobId)).willReturn(feedbackDownloadJob);
        given(organizationRepository.findByUuid(organizationUuid)).willReturn(Optional.empty());

        // when
        feedbackFileDownloadService.createAndUploadFileAsync(jobId, organizationUuid);

        // then
        verify(feedbackDownloadJobStore).getById(jobId);
        verify(organizationRepository).findByUuid(organizationUuid);
        verify(feedbackDownloadJob).fail(anyString());
        verify(s3UploadService, never()).uploadFile(anyString(), anyString(), anyString(), any(byte[].class));
    }

    @Test
    @DisplayName("Excel 다운로드 중 예외 발생 시 작업을 실패 처리한다")
    void createAndUploadFileAsync_excelDownloadException_failsJob() throws Exception {
        // given
        String jobId = UUID.randomUUID().toString();
        UUID organizationUuid = UUID.randomUUID();
        List<Feedback> feedbacks = Arrays.asList(feedback1, feedback2);
        RuntimeException exception = new RuntimeException("Excel 생성 실패");

        given(feedbackDownloadJobStore.getById(jobId)).willReturn(feedbackDownloadJob);
        given(organizationRepository.findByUuid(organizationUuid)).willReturn(Optional.of(organization));
        given(feedBackRepository.findByOrganization(organization)).willReturn(feedbacks);

        // when
        feedbackFileDownloadService.createAndUploadFileAsync(jobId, organizationUuid);

        // then
        verify(feedbackDownloadJobStore).getById(jobId);
        verify(organizationRepository).findByUuid(organizationUuid);
        verify(feedBackRepository).findByOrganization(organization);
    }

    @Test
    @DisplayName("S3 업로드 중 예외 발생 시 작업을 실패 처리한다")
    void createAndUploadFileAsync_s3UploadException_failsJob() throws Exception {
        // given
        String jobId = UUID.randomUUID().toString();
        UUID organizationUuid = UUID.randomUUID();
        List<Feedback> feedbacks = Arrays.asList(feedback1, feedback2);
        RuntimeException exception = new RuntimeException("S3 업로드 실패");

        given(feedbackDownloadJobStore.getById(jobId)).willReturn(feedbackDownloadJob);
        given(organizationRepository.findByUuid(organizationUuid)).willReturn(Optional.of(organization));
        given(feedBackRepository.findByOrganization(organization)).willReturn(feedbacks);
        given(s3UploadService.uploadFile(eq("xlsx"), eq("feedback_file"), eq(jobId), any(byte[].class)))
                .willThrow(exception);

        // when
        feedbackFileDownloadService.createAndUploadFileAsync(jobId, organizationUuid);

        // then
        verify(feedbackDownloadJobStore).getById(jobId);
        verify(organizationRepository).findByUuid(organizationUuid);
        verify(feedBackRepository).findByOrganization(organization);
        verify(feedbackExcelDownloader).download(eq(organization), eq(feedbacks), any(ByteArrayOutputStream.class), eq(jobId));
        verify(s3UploadService).uploadFile(eq("xlsx"), eq("feedback_file"), eq(jobId), any(byte[].class));
        verify(feedbackDownloadJob).fail(anyString());
        verify(feedbackDownloadJob, never()).completeWithUrl(anyString());
    }

    @Test
    @DisplayName("일반 예외 발생 시 작업을 실패 처리한다")
    void createAndUploadFileAsync_generalException_failsJob() {
        // given
        String jobId = UUID.randomUUID().toString();
        UUID organizationUuid = UUID.randomUUID();
        RuntimeException exception = new RuntimeException("예상치 못한 오류");

        given(feedbackDownloadJobStore.getById(jobId)).willReturn(feedbackDownloadJob);
        given(organizationRepository.findByUuid(organizationUuid)).willThrow(exception);

        // when
        feedbackFileDownloadService.createAndUploadFileAsync(jobId, organizationUuid);

        // then
        verify(feedbackDownloadJobStore).getById(jobId);
        verify(organizationRepository).findByUuid(organizationUuid);
        verify(feedbackDownloadJob).fail(anyString());
    }
}
