import { postOrgFileDownload } from '@/apis/adminFeedback.api';
import { useMutation } from '@tanstack/react-query';

interface UseStartDownloadFeedbacksProps {
  organizationId: string;
  setJobId: (data: string) => void;
}

export default function useStartDownloadFeedbacks({
  organizationId,
  setJobId,
}: UseStartDownloadFeedbacksProps) {
  return useMutation({
    mutationFn: () =>
      postOrgFileDownload({
        organizationUuid: organizationId,
      }),
    onSuccess: (response) => {
      setJobId(response.data.jobId);
    },
  });
}
