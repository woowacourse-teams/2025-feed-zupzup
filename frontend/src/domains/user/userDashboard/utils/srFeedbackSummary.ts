import { FeedbackType } from '@/types/feedback.types';

export function toPlainText(s: string, max = 120) {
  const text =
    s
      ?.replace(/<[^>]+>/g, ' ')
      ?.replace(/\s+/g, ' ')
      ?.trim() || '';
  return text.length > max ? `${text.slice(0, max)}…` : text;
}

export function formatDateForSR(iso?: string) {
  if (!iso) return '';
  try {
    const d = new Date(iso);

    return `${d.getFullYear()}년 ${d.getMonth() + 1}월 ${d.getDate()}일 ${d.getHours()}시 ${String(d.getMinutes()).padStart(2, '0')}분`;
  } catch {
    return '';
  }
}

export function srFeedbackSummary({
  feedback,
  myFeedback,
  postedAt,
  isAdmin,
}: {
  feedback: FeedbackType;
  myFeedback: boolean;
  postedAt: string;
  isAdmin: boolean;
}) {
  const opts: {
    isMine: boolean;
    userName?: string;
    status: string;
    category: string;
    postedAt?: string;
    likeCount?: number;
    commentCount?: number;
    isSecret: boolean;
    hasImage: boolean;
    comment: string | null;
    content?: string;
  } = {
    isMine: myFeedback,
    userName: feedback.userName,
    status: feedback.status,
    category: feedback.category,
    postedAt: feedback.postedAt,
    likeCount: feedback.likeCount,
    commentCount: feedback.comment ? feedback.comment.length : 0,
    isSecret: feedback.isSecret,
    hasImage: (feedback.imageUrl && feedback.imageUrl.length > 0) || false,
    comment: feedback.comment,
    content: feedback.content,
  };

  const parts: string[] = [];

  if (opts.content) {
    parts.push(`내용: ${toPlainText(opts.content)}`);
  }

  if (opts.isMine) parts.push('내가 작성한 피드백');

  if (opts.userName) parts.push(`작성자 ${opts.userName}`);

  if (opts.isSecret) parts.push(opts.isSecret && '비밀글입니다');

  if (opts.comment && (isAdmin || !opts.isSecret))
    parts.push('관리자 답변이 존재합니다.  ' + opts.comment);

  if (postedAt) parts.push(`작성일 ${postedAt}`);

  if (isAdmin || (!opts.isSecret && typeof opts.commentCount === 'number'))
    parts.push(`좋아요 ${opts.likeCount ?? 0}개`);

  if (opts.hasImage) parts.push('이미지 포함');

  return parts.join(', ');
}
