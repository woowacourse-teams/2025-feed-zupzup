import { getOrganizationFeedbacksFile } from '@/apis/adminFeedback.api';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import { useQuery } from '@tanstack/react-query';

export const useDownloadFeedbacks = (organizationId: string) => {
  const { groupName } = useOrganizationName({
    organizationId,
  });

  return useQuery({
    queryKey: ['organizationFeedbacksFile', organizationId],
    queryFn: async () => {
      const response = await getOrganizationFeedbacksFile({
        organizationUuid: organizationId,
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
    enabled: false,
  });
};

export default useDownloadFeedbacks;
