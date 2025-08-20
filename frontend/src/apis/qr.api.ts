import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/apiResponse';
import { QRCodeData } from '@/types/qr.types';

type QRCodeResponse = ApiResponse<QRCodeData>;

interface GetQRCodeParams {
  organizationId: string;
}
interface QRDownloadData {
  downloadUrl: string;
}

export async function getQRCode({ organizationId }: GetQRCodeParams) {
  const response = await apiClient.get<QRCodeResponse>(
    `/admin/organizations/${organizationId}/qr-code`
  );
  return response as QRCodeResponse;
}

interface GetQRDownloadUrlParams {
  organizationId: string;
}

type QRDownloadUrlResponse = ApiResponse<QRDownloadData>;

export async function getQRDownloadUrl({
  organizationId,
}: GetQRDownloadUrlParams) {
  const response = await apiClient.get<QRDownloadUrlResponse>(
    `/admin/organizations/${organizationId}/qr-code/download-url`
  );
  return response as QRDownloadUrlResponse;
}
