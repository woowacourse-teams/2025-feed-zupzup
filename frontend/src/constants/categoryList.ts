export const CATEGORY_LIST = [
  {
    icon: '🚨',
    category: '신고',
  },
  {
    icon: '❓',
    category: '질문',
  },
  {
    icon: '💬',
    category: '건의',
  },
  {
    icon: '📝',
    category: '피드백',
  },
  {
    icon: '👍',
    category: '칭찬',
  },
  {
    icon: '📢',
    category: '정보공유',
  },
  {
    icon: '📂',
    category: '기타',
  },
] as const;

export type CategoryListType = (typeof CATEGORY_LIST)[number]['category'];
