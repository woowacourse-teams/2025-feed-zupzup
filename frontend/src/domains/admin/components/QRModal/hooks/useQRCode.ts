import { getQRCode } from '@/apis/qr.api';
import { QUERY_KEYS } from '@/constants/queryKeys';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { useErrorModalActions } from '@/hooks/useErrorModal';
import { useQuery } from '@tanstack/react-query';

export const ONE_DAY = 1000 * 60 * 60 * 24;

export const useQRCode = () => {
  const { showErrorModal } = useErrorModalActions();
  const { organizationId } = useOrganizationId();

  const {
    data: QRcode,
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: QUERY_KEYS.qrCode(organizationId),
    queryFn: () => getQRCode({ organizationId }),
    staleTime: ONE_DAY,
    gcTime: ONE_DAY,
    enabled: !!organizationId,
  });

  if (isError) {
    showErrorModal(error, 'QR 코드 조회 실패');
  }

  return { data: QRcode?.data, isLoading };
};
