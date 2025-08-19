export interface QRCodeData {
  imageUrl: string;
  siteUrl: string;
}

export interface QRCodeResponse {
  data: QRCodeData;
  status: number;
  message: string;
}

export interface GetQRCodeParams {
  organizationId: string;
}
