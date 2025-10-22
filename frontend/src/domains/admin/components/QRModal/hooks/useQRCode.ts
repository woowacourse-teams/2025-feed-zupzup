import { getQRCode } from '@/apis/qr.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useQuery } from '@tanstack/react-query';

export const ONE_DAY = 1000 * 60 * 60 * 24;

export const useQRCode = () => {
  const { organizationId } = useOrganizationId();

  const { data: QRcode, isLoading } = useQuery({
    queryKey: QUERY_KEYS.qrCode(organizationId),
    queryFn: () => getQRCode({ organizationId }),
    staleTime: ONE_DAY,
    gcTime: ONE_DAY,
    enabled: !!organizationId,
    retry: false,
  });

  return { data: QRcode?.data, isLoading };
};
