import { useState, useEffect } from 'react';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { getQRCode } from '@/apis/qr.api';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import { QRCodeData } from '@/types/qr.types';

export const useQRCode = () => {
  const [data, setData] = useState<QRCodeData | null>(null);
  const [loading, setLoading] = useState(false);
  const { showErrorModal } = useErrorModalContext();
  const { organizationId } = useOrganizationId();

  useEffect(() => {
    const fetchQRCode = async () => {
      setLoading(true);

      try {
        const result = await getQRCode({ organizationId });
        setData(result.data);
      } catch (err) {
        showErrorModal(err, 'QR 코드 조회 실패');
      } finally {
        setLoading(false);
      }
    };

    fetchQRCode();
  }, []);

  return { data, loading };
};
