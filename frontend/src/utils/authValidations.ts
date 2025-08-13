import {
  lengthAtLeast,
  lengthBetween,
  mustMatch,
  mustNotMatch,
} from '@/utils/validation';

type ValidationState = { ok: true } | { ok: false; message: string };

const REGEX = {
  ONLY_ALNUM: /^[a-zA-Z0-9]+$/,
  ALPHA: /[a-zA-Z]/,
  KOREAN_ENGLISH: /^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]+$/,
  SPACE: /\s/,
};

export const validateId = (value: string): ValidationState => {
  const min = 5;
  const max = 10;

  const rules = [
    lengthBetween(
      min,
      max,
      `아이디는 ${min}자 이상 ${max}글자 이하로 작성해주세요`
    ),
    mustMatch(
      REGEX.ONLY_ALNUM,
      '아이디는 영문 대소문자와 숫자만 사용할 수 있습니다.'
    ),
    mustNotMatch(REGEX.SPACE, '아이디에 공백을 포함할 수 없습니다.'),
  ];

  for (const rule of rules) {
    const result = rule(value);
    if (!result.ok) return result; // 첫 에러 반환
  }
  return { ok: true };
};

export const validatePassword = (value: string): ValidationState => {
  const min = 5;

  const rules = [
    lengthAtLeast(5, `비밀번호는 ${min}자 이상으로 작성해주세요`),
    mustMatch(REGEX.ALPHA, '비밀번호는 영문 대소문자만 사용할 수 있습니다.'),
    mustNotMatch(REGEX.SPACE, '비밀번호에 공백을 포함할 수 없습니다.'),
  ];

  for (const rule of rules) {
    const result = rule(value);
    if (!result.ok) return result;
  }
  return { ok: true };
};

export const validatePasswordConfirm = (
  value: string,
  password: string
): ValidationState => {
  if (password && value !== password) {
    return { ok: false, message: '비밀번호가 일치하지 않습니다.' };
  }
  return { ok: true };
};

export const validateName = (value: string): ValidationState => {
  const min = 1;
  const max = 10;

  const rules = [
    lengthBetween(
      min,
      max,
      `이름은 ${min}자 이상 ${max}글자 이하로 작성해주세요`
    ),
    mustMatch(REGEX.KOREAN_ENGLISH, '이름은 한글과 영문만 사용할 수 있습니다.'),
    mustNotMatch(REGEX.SPACE, '이름에 공백을 포함할 수 없습니다.'),
  ];

  for (const rule of rules) {
    const result = rule(value);
    if (!result.ok) return result;
  }
  return { ok: true };
};
