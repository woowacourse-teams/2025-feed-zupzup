import useDownloadFeedbacksFile from '@/components/Header/hooks/useDownloadFeedbacksFile';
import usePollingFeedbackDownloadStatus from '@/components/Header/hooks/usePollingFeedbackDownloadStatus';
import { useToast } from '@/contexts/useToast';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import usePreventWindowClose from '@/domains/hooks/usePreventWindowClose';
import { useEffect, useState } from 'react';

export default function useDownloadFeedbacks() {
  const { organizationId } = useOrganizationId();
  const [jobId, setJobId] = useState<string>('');
  const { showToast } = useToast();

  const { data: feedbackDownloadStatus } = usePollingFeedbackDownloadStatus({
    jobId,
    organizationId,
  });

  const { refetch: fileDownload } = useDownloadFeedbacksFile({
    organizationId,
    jobId,
    downloadEnabled: feedbackDownloadStatus?.jobStatus === 'COMPLETED',
  });

  usePreventWindowClose({
    isDownloading: feedbackDownloadStatus?.jobStatus === 'PROCESSING',
  });

  useEffect(() => {
    const handleDownload = async () => {
      if (feedbackDownloadStatus?.jobStatus === 'FAILED') {
        showToast(
          '피드백 데이터 다운로드에 실패했습니다. 잠시후 다시 시도해주세요',
          'error',
          3000
        );
      }

      if (feedbackDownloadStatus?.jobStatus === 'COMPLETED') {
        console.log('download start');
        await fileDownload();
        showToast('피드백 데이터가 다운로드되었습니다.', 'success', 3000);
      }
    };

    handleDownload();
  }, [showToast, feedbackDownloadStatus, fileDownload]);

  return {
    feedbackDownloadStatus,
    setJobId,
  };
}
