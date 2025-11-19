package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackDownloadJobStore;
import feedzupzup.backend.feedback.domain.FeedbackExcelDownloader;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.s3.service.S3UploadService;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FeedbackFileDownloadService {

    private final FeedbackDownloadJobStore feedbackDownloadJobStore;
    private final OrganizationRepository organizationRepository;
    private final FeedbackRepository feedBackRepository;
    private final FeedbackExcelDownloader feedbackExcelDownloader;
    private final S3UploadService s3UploadService;

    @Async
    public void createAndUploadFileAsync(final String jobId, final UUID organizationUuid) {
        final FeedbackDownloadJob job = feedbackDownloadJobStore.getById(jobId);
        if (job == null) {
            log.error("작업을 찾을 수 없습니다. jobId={}", jobId);
            return;
        }

        try {
            final Organization organization = organizationRepository.findByUuid(organizationUuid)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "해당 ID(id = " + organizationUuid + ")인 단체를 찾을 수 없습니다."));

            final List<Feedback> feedbacks = feedBackRepository.findByOrganization(organization);

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            feedbackExcelDownloader.download(organization, feedbacks, byteArrayOutputStream, jobId);
            final byte[] excelData = byteArrayOutputStream.toByteArray();

            final String s3Url = s3UploadService.uploadFile("xlsx", "feedback_file", jobId, excelData);

            job.completeWithUrl(s3Url);

        } catch (Exception e) {
            log.error("피드백 엑셀 파일 생성 중 오류 발생. jobId={}", jobId, e);
            job.fail("파일 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
