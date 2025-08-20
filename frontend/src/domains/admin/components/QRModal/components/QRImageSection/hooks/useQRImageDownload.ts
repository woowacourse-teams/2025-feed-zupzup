import { getQRDownloadUrl } from '@/apis/qr.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useQuery } from '@tanstack/react-query';

export function useQRImageDownload() {
  const { organizationId } = useOrganizationId();

  const query = useQuery({
    queryKey: [...QUERY_KEYS.qrImageDownload, organizationId],
    queryFn: () => getQRDownloadUrl({ organizationId }),
    select: (response) => response.data.downloadUrl,
  });

  return {
    downloadUrl: query.data,
  };
}
