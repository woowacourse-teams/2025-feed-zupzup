export const FETCH_ERROR_MESSAGE: Record<string, string> = {
  400: '잘못된 요청입니다. 다시 시도해주세요.',
  401: '접근 권한이 없습니다. 인증이 필요합니다.',
  403: '접근 권한이 없습니다. 관리자에게 문의해주세요.',
  404: '요청하신 주소를 찾을 수 없습니다. 다시 시도해주세요.',
  500: '서버에 문제가 발생했습니다. 잠시 후에 다시 시도해주세요.',
} as const;

export const DEFAULT_ERROR_MESSAGE = '저런! 예기치 못한 문제가 발생했어요!';
