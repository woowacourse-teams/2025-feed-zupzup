export const downloadImage = async (
  url: string,
  fileName: string = 'image.png'
): Promise<void> => {
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error('이미지 다운로드에 실패했습니다.');
  }

  const blob = await response.blob();
  const downloadUrl = window.URL.createObjectURL(blob);

  const link = document.createElement('a');
  link.href = downloadUrl;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();

  document.body.removeChild(link);
  window.URL.revokeObjectURL(downloadUrl);
};

export const downloadQRCode = async (url: string): Promise<void> => {
  return downloadImage(url, 'qr-code.png');
};
