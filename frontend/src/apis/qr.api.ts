import { apiClient } from '@/apis/apiClient';
import {
  GetQRCodeParams,
  QRCodeResponse,
  QRDownloadUrlResponse,
} from '@/types/qr.types';

export async function getQRCode({ organizationId }: GetQRCodeParams) {
  const response = await apiClient.get<QRCodeResponse>(
    `/admin/organizations/${organizationId}/qr-code`
  );
  return response as QRCodeResponse;
}

interface GetQRDownloadUrlParams {
  organizationId: string;
}

export async function getQRDownloadUrl({
  organizationId,
}: GetQRDownloadUrlParams) {
  const response = await apiClient.get<QRDownloadUrlResponse>(
    `/admin/organizations/${organizationId}/qr-code/download-url`
  );
  return response as QRDownloadUrlResponse;
}
