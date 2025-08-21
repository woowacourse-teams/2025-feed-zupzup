export function formatRelativeTime(isoDate: string): string {
  const now = new Date();
  const date = new Date(isoDate);
  const diffMs = now.getTime() - date.getTime();

  const seconds = Math.floor(diffMs / 1000);
  const minutes = Math.floor(diffMs / (1000 * 60));
  const hours = Math.floor(diffMs / (1000 * 60 * 60));
  const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  const weeks = Math.floor(days / 7);
  const months = Math.floor(days / 30);

  if (seconds < 60) return '방금전';
  if (minutes < 60) return `${minutes}분전`;
  if (hours < 24) return `${hours}시간전`;
  if (days < 7) return `${days}일전`;
  if (weeks < 4) return `${weeks}주전`;
  return `${months}개월전`;
}
