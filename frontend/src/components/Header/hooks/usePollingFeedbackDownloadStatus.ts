import { getOrgFileDownloadStatus } from '@/apis/adminFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useQuery } from '@tanstack/react-query';

interface UsePollingFeedbackDownloadStatusProps {
  jobId: string;
  organizationId: string;
  pollingEnabled?: boolean;
}

export default function usePollingFeedbackDownloadStatus({
  jobId,
  organizationId,
}: UsePollingFeedbackDownloadStatusProps) {
  return useQuery({
    queryKey: QUERY_KEYS.feedbackDownloadStatus(jobId, organizationId),
    queryFn: async () =>
      await getOrgFileDownloadStatus({
        organizationUuid: organizationId,
        jobId,
      }),
    select: (response) => response.data,
    enabled: jobId !== '',
    refetchInterval: (query) => {
      console.log('refetching...', query);
      const data = query.state.data;
      if (!data) return 2000;
      if (
        data.data.jobStatus === 'COMPLETED' ||
        data.data.jobStatus === 'FAILED'
      ) {
        return false;
      }
      return 2000;
    },
  });
}
