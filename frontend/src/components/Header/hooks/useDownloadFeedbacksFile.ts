import { getOrgFileDownload } from '@/apis/adminFeedback.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import { useQuery } from '@tanstack/react-query';

interface UseDownloadFeedbacksFileProps {
  organizationId: string;
  jobId: string;
  downloadEnabled?: boolean;
}

export default function useDownloadFeedbacksFile({
  organizationId,
  jobId,
  downloadEnabled,
}: UseDownloadFeedbacksFileProps) {
  const { groupName } = useOrganizationName({
    organizationId,
  });

  return useQuery({
    queryKey: QUERY_KEYS.organizationFeedbacksFile(jobId, organizationId),
    queryFn: async () => {
      const response = await getOrgFileDownload({
        organizationUuid: organizationId,
        jobId,
      });

      if (!response) {
        throw new Error('피드백 파일을 다운로드하는 데 실패했습니다.');
      }

      const now = new Date();
      const formattedDate = now.toISOString().split('T')[0];
      const safeOrgName = groupName.replace(/[\\/:*?"<>|]/g, '_');
      const filename = `${safeOrgName}_feedbacks_${formattedDate}.xlsx`;

      const url = window.URL.createObjectURL(response);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      a.click();

      window.URL.revokeObjectURL(url);
      return true;
    },
    enabled: jobId !== '' && downloadEnabled === true,
  });
}
