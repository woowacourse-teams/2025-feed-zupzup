import { getQRDownloadUrl } from '@/apis/qr.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { ONE_DAY } from '@/domains/admin/components/QRModal/hooks/useQRCode';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useQuery } from '@tanstack/react-query';

export function useQRImageDownload() {
  const { organizationId } = useOrganizationId();

  const query = useQuery({
    queryKey: [...QUERY_KEYS.qrImageDownload, organizationId],
    queryFn: () => getQRDownloadUrl({ organizationId }),
    select: (response) => response.data.downloadUrl,
    staleTime: ONE_DAY,
    gcTime: ONE_DAY,
  });

  return {
    downloadUrl: query.data,
  };
}
