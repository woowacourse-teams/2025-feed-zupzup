import { getQRDownloadUrl } from '@/apis/qr.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useQuery } from '@tanstack/react-query';

export function useQRImageDownload() {
  const { organizationId } = useOrganizationId();
  const { showErrorModal } = useErrorModalContext();

  const query = useQuery({
    queryKey: [...QUERY_KEYS.qrImageDownload, organizationId],
    queryFn: () => getQRDownloadUrl({ organizationId }),
    select: (response) => response.data.downloadUrl,
  });

  if (query.isError) {
    showErrorModal(
      'QR 이미지를 다운로드하는데 실패했습니다. 다시 시도해 주세요.',
      '에러'
    );
  }

  return {
    downloadUrl: query.data,
  };
}
