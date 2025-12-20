import useDownloadFeedbacksFile from '@/components/Header/hooks/useDownloadFeedbacksFile';
import usePollingFeedbackDownloadStatus from '@/components/Header/hooks/usePollingFeedbackDownloadStatus';
import { useToast } from '@/contexts/useToast';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import usePreventWindowClose from '@/domains/hooks/usePreventWindowClose';
import { useEffect, useRef, useState } from 'react';
import { FileDownloadStatus } from '../../../apis/adminFeedback.api';

export default function useDownloadFeedbacks() {
  const { organizationId } = useOrganizationId();
  const [jobId, setJobId] = useState<string>('');
  const { showToast } = useToast();
  const prevStatusRef = useRef<FileDownloadStatus | null>(null);

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
    const currentStatus = feedbackDownloadStatus?.jobStatus;

    if (!currentStatus || prevStatusRef.current === currentStatus) {
      return;
    }

    const handleDownload = async () => {
      if (currentStatus === 'FAILED') {
        showToast(
          '피드백 데이터 다운로드에 실패했습니다. 잠시후 다시 시도해주세요',
          'error',
          3000
        );
        prevStatusRef.current = 'FAILED';
        return;
      }

      if (currentStatus === 'COMPLETED') {
        try {
          await fileDownload();
          showToast('피드백 데이터가 다운로드되었습니다.', 'success', 3000);
          prevStatusRef.current = 'COMPLETED';
        } catch {
          showToast('파일 다운로드 중 오류가 발생했습니다.', 'error', 3000);
        }
      }
    };

    handleDownload();
  }, [feedbackDownloadStatus?.jobStatus, fileDownload, showToast]);

  return {
    feedbackDownloadStatus,
    setJobId,
  };
}
