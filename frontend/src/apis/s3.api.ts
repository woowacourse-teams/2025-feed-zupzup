import { apiClient } from '@/apis/apiClient';

export const IMAGE_EXTENSIONS = [
  'jpg',
  'jpeg',
  'jpe',
  'jif',
  'jfif',
  'jfi',
  'png',
  'gif',
  'webp',
  'tiff',
  'tif',
  'bmp',
  'svg',
  'svgz',
  'ico',
  'heic',
  'heif',
  'raw',
  'arw',
  'cr2',
  'nrw',
  'k25',
  'psd',
] as const;

export type ImageExtension = (typeof IMAGE_EXTENSIONS)[number];

export interface PostPresignedUrlParams {
  extension: ImageExtension;
  objectDir: string;
}

interface PresignedUrlResponse {
  data: {
    presignedUrl: string;
    contentType: string;
  };
}

export async function postPresignedUrl({
  extension,
  objectDir,
}: PostPresignedUrlParams): Promise<PresignedUrlResponse> {
  const response = await apiClient.post('/presigned-url', {
    extension,
    objectDir,
  });
  return response as PresignedUrlResponse;
}

export async function uploadImageToS3({
  presignedUrl,
  file,
  contentType,
}: {
  presignedUrl: string;
  file: File;
  contentType: string;
}) {
  const response = await fetch(presignedUrl, {
    method: 'PUT',
    headers: {
      'Content-Type': contentType,
    },
    body: file,
  });

  if (!response.ok) {
    throw new Error(
      `S3 upload failed: ${response.status} ${response.statusText}`
    );
  }

  return response;
}
