import { ApiResponse } from './apiResponse';

export interface QRCodeData {
  imageUrl: string;
  siteUrl: string;
}

export type QRCodeResponse = ApiResponse<QRCodeData>;

export interface GetQRCodeParams {
  organizationId: string;
}
export interface QRDownloadData {
  downloadUrl: string;
}

export type QRDownloadUrlResponse = ApiResponse<QRDownloadData>;
