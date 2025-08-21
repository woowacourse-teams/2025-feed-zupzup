interface UrlDownloadProps {
  downloadUrl: string;
  fileName: string;
}

export function urlDownload({ downloadUrl, fileName }: UrlDownloadProps) {
  const link = document.createElement('a');
  link.href = downloadUrl;
  link.download = fileName;
  link.style.display = 'none';

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}
