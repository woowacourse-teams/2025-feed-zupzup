import { getQRDownloadUrl } from '@/apis/qr.api';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useQuery } from '@tanstack/react-query';

export function useQRImageDownload() {
  const { organizationId } = useOrganizationId();

  const query = useQuery({
    queryKey: ['qrImageDownload', organizationId],
    queryFn: () => getQRDownloadUrl({ organizationId }),
    select: (response) => response.data.downloadUrl,
  });

  return {
    downloadUrl: query.data,
  };
}
