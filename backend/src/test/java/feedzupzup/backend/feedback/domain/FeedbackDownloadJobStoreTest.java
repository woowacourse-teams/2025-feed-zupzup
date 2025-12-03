package feedzupzup.backend.feedback.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedbackDownloadJobStoreTest {

    private FeedbackDownloadJobStore feedbackDownloadJobStore;

    @BeforeEach
    void setUp() {
        feedbackDownloadJobStore = new FeedbackDownloadJobStore();
    }

    @Test
    @DisplayName("Job을 저장하고 조회할 수 있다")
    void save_and_getById_success() {
        // given
        String organizationUuid = UUID.randomUUID().toString();
        FeedbackDownloadJob job = FeedbackDownloadJob.create(organizationUuid);

        // when
        FeedbackDownloadJob savedJob = feedbackDownloadJobStore.save(job);
        FeedbackDownloadJob foundJob = feedbackDownloadJobStore.getById(job.getJobId());

        // then
        assertThat(savedJob).isEqualTo(job);
        assertThat(foundJob).isEqualTo(job);
        assertThat(foundJob.getJobId()).isEqualTo(job.getJobId());
    }

    @Test
    @DisplayName("존재하지 않는 jobId로 조회하면 null을 반환한다")
    void getById_notFound_returnsNull() {
        // given
        String nonExistentJobId = UUID.randomUUID().toString();

        // when
        FeedbackDownloadJob result = feedbackDownloadJobStore.getById(nonExistentJobId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("여러 개의 Job을 저장하고 각각 조회할 수 있다")
    void save_multipleJobs_success() {
        // given
        FeedbackDownloadJob job1 = FeedbackDownloadJob.create(UUID.randomUUID().toString());
        FeedbackDownloadJob job2 = FeedbackDownloadJob.create(UUID.randomUUID().toString());
        FeedbackDownloadJob job3 = FeedbackDownloadJob.create(UUID.randomUUID().toString());

        // when
        feedbackDownloadJobStore.save(job1);
        feedbackDownloadJobStore.save(job2);
        feedbackDownloadJobStore.save(job3);

        // then
        assertThat(feedbackDownloadJobStore.getById(job1.getJobId())).isEqualTo(job1);
        assertThat(feedbackDownloadJobStore.getById(job2.getJobId())).isEqualTo(job2);
        assertThat(feedbackDownloadJobStore.getById(job3.getJobId())).isEqualTo(job3);
    }

    @Test
    @DisplayName("동일한 jobId로 저장하면 덮어쓴다")
    void save_sameJobId_overwrites() {
        // given
        String organizationUuid = UUID.randomUUID().toString();
        FeedbackDownloadJob job1 = FeedbackDownloadJob.create(organizationUuid);
        String jobId = job1.getJobId();

        feedbackDownloadJobStore.save(job1);

        FeedbackDownloadJob job2 = FeedbackDownloadJob.create(organizationUuid);

        // when
        feedbackDownloadJobStore.save(job1);
        FeedbackDownloadJob foundJob = feedbackDownloadJobStore.getById(jobId);

        // then
        assertThat(foundJob).isEqualTo(job1);
    }

    @Test
    @DisplayName("Job의 진행률을 업데이트할 수 있다")
    void updateProgress_existingJob_updatesProgress() {
        // given
        String organizationUuid = UUID.randomUUID().toString();
        FeedbackDownloadJob job = FeedbackDownloadJob.create(organizationUuid);
        feedbackDownloadJobStore.save(job);

        int currentRow = 50;
        int totalRow = 100;
        int expectedProgress = 50;

        // when
        feedbackDownloadJobStore.updateProgress(job.getJobId(), currentRow, totalRow);

        // then
        FeedbackDownloadJob updatedJob = feedbackDownloadJobStore.getById(job.getJobId());
        assertThat(updatedJob.getProgress()).isEqualTo(expectedProgress);
    }

    @Test
    @DisplayName("존재하지 않는 Job의 진행률을 업데이트해도 예외가 발생하지 않는다")
    void updateProgress_nonExistingJob_doesNotThrow() {
        // given
        String nonExistentJobId = UUID.randomUUID().toString();

        // when & then
        assertDoesNotThrow(
                () -> feedbackDownloadJobStore.updateProgress(nonExistentJobId, 50, 100)
        );
    }

    @Test
    @DisplayName("진행률을 0%로 업데이트할 수 있다")
    void updateProgress_zeroPercent_success() {
        // given
        String organizationUuid = UUID.randomUUID().toString();
        FeedbackDownloadJob job = FeedbackDownloadJob.create(organizationUuid);
        feedbackDownloadJobStore.save(job);

        // when
        feedbackDownloadJobStore.updateProgress(job.getJobId(), 0, 100);

        // then
        FeedbackDownloadJob updatedJob = feedbackDownloadJobStore.getById(job.getJobId());
        assertThat(updatedJob.getProgress()).isEqualTo(0);
    }

    @Test
    @DisplayName("진행률을 100%로 업데이트할 수 있다")
    void updateProgress_hundredPercent_success() {
        // given
        String organizationUuid = UUID.randomUUID().toString();
        FeedbackDownloadJob job = FeedbackDownloadJob.create(organizationUuid);
        feedbackDownloadJobStore.save(job);

        // when
        feedbackDownloadJobStore.updateProgress(job.getJobId(), 100, 100);

        // then
        FeedbackDownloadJob updatedJob = feedbackDownloadJobStore.getById(job.getJobId());
        assertThat(updatedJob.getProgress()).isEqualTo(100);
    }

    @Test
    @DisplayName("진행률 계산 시 소수점은 버림 처리된다")
    void updateProgress_roundsDown_success() {
        // given
        String organizationUuid = UUID.randomUUID().toString();
        FeedbackDownloadJob job = FeedbackDownloadJob.create(organizationUuid);
        feedbackDownloadJobStore.save(job);

        // when
        feedbackDownloadJobStore.updateProgress(job.getJobId(), 33, 100);

        // then
        FeedbackDownloadJob updatedJob = feedbackDownloadJobStore.getById(job.getJobId());
        assertThat(updatedJob.getProgress()).isEqualTo(33);
    }

    @Test
    @DisplayName("진행률 계산 시 중간값도 정확히 계산된다")
    void updateProgress_midValue_success() {
        // given
        String organizationUuid = UUID.randomUUID().toString();
        FeedbackDownloadJob job = FeedbackDownloadJob.create(organizationUuid);
        feedbackDownloadJobStore.save(job);

        // when
        feedbackDownloadJobStore.updateProgress(job.getJobId(), 75, 150);

        // then
        FeedbackDownloadJob updatedJob = feedbackDownloadJobStore.getById(job.getJobId());
        assertThat(updatedJob.getProgress()).isEqualTo(50);
    }
}
