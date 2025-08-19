import { apiClient } from '@/apis/apiClient';
import { GetQRCodeParams, QRCodeResponse } from '@/types/qr.types';

export async function getQRCode({ organizationId }: GetQRCodeParams) {
  const response = await apiClient.get<QRCodeResponse>(
    `/admin/organizations/${organizationId}/qr-code`
  );
  return response as QRCodeResponse;
}
