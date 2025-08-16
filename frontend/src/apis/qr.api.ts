import { apiClient } from '@/apis/apiClient';

interface QRCodeData {
  imageUrl: string;
  siteUrl: string;
}

interface QRCodeResponse {
  data: QRCodeData;
  status: number;
  message: string;
}

interface GetQRCodeParams {
  organizationId: number;
}

export async function getQRCode({ organizationId }: GetQRCodeParams) {
  const response = await apiClient.get<QRCodeResponse>(
    `/admin/organizations/${organizationId}/qr-code`
  );
  return response as QRCodeResponse;
}
