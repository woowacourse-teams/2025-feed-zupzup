export const ERROR_MESSAGES = {
  UNKNOWN_ERROR: {
    title: '알 수 없는 오류',
    subtitle: '알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
    button: '재시도',
    onClick: () => window.location.reload(),
  },
  AUTH_ERROR: {
    title: '인증 오류',
    subtitle: '인증이 필요합니다. 로그인 후 다시 시도해주세요.',
    button: '로그인하기',
    onClick: () => window.location.reload(),
  },
  SERVER_ERROR: {
    title: '서버 오류',
    subtitle: '서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.',
    button: '재시도',
    onClick: () => window.location.reload(),
  },
  FAULT_REQUEST: {
    title: '잘못된 요청',
    subtitle: '잘못된 요청입니다. 다시 시도해주세요.',
    button: '재시도',
    onClick: () => window.location.reload(),
  },
  NETWORK_ERROR: {
    title: '네트워크 오류',
    subtitle: '네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
    button: '재시도',
    onClick: () => window.location.reload(),
  },
};
