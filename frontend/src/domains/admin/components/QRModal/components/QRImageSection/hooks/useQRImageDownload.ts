import { getQRDownloadUrl } from '@/apis/qr.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { ONE_DAY } from '@/domains/admin/components/QRModal/hooks/useQRCode';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useErrorModal } from '@/hooks/useErrorModal';
import { useQuery } from '@tanstack/react-query';

export function useQRImageDownload() {
  const { organizationId } = useOrganizationId();
  const { showErrorModal } = useErrorModal();

  const query = useQuery({
    queryKey: [...QUERY_KEYS.qrImageDownload, organizationId],
    queryFn: () => getQRDownloadUrl({ organizationId }),
    select: (response) => response.data.downloadUrl,
    staleTime: ONE_DAY,
    gcTime: ONE_DAY,
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
