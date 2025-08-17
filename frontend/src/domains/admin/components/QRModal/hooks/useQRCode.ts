import { useState, useEffect } from 'react';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { getQRCode } from '@/apis/qr.api';

interface QRCodeData {
  imageUrl: string;
  siteUrl: string;
}

export const useQRCode = () => {
  const [data, setData] = useState<QRCodeData | null>(null);
  const [loading, setLoading] = useState(false);
  const { showErrorModal } = useErrorModalContext();

  useEffect(() => {
    const fetchQRCode = async () => {
      setLoading(true);

      try {
        const result = await getQRCode({ organizationId: '1' });
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
