package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class FeedbackDownloadJobStore {

    private final ConcurrentHashMap<String, FeedbackDownloadJob> jobs = new ConcurrentHashMap<>();

    public FeedbackDownloadJob save(final FeedbackDownloadJob job) {
        jobs.put(job.getJobId(), job);
        return job;
    }

    public FeedbackDownloadJob getById(final String jobId) {
        return jobs.get(jobId);
    }

    public void updateProgress(final String jobId, final int currentRow, final int totalRow) {
        final FeedbackDownloadJob targetJob = jobs.get(jobId);
        if (targetJob != null) {
            final int progress = (int) (((double) currentRow / totalRow) * 100);
            targetJob.updateProgress(progress);
        }
    }
}
