import { ValidationState } from '@/types/validateState.types';
import {
  lengthAtLeast,
  lengthBetween,
  mustMatch,
  mustNotMatch,
} from '@/utils/validation';

const REGEX = {
  ONLY_ALNUM: /^[a-zA-Z0-9]+$/,
  ALPHA: /[a-zA-Z]/,
  KOREAN_ENGLISH: /^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]+$/,
  SPACE: /\s/,
};

export const VALIDATION_RULES = {
  ID: { min: 5, max: 20 },
  PASSWORD: { min: 5 },
  NAME: { min: 1, max: 10 },
};

export const validateId = (value: string): ValidationState => {
  const rules = [
    lengthBetween(
      VALIDATION_RULES.ID.min,
      VALIDATION_RULES.ID.max,
      `아이디는 ${VALIDATION_RULES.ID.min}자 이상 ${VALIDATION_RULES.ID.max}글자 이하로 작성해주세요`
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
  const rules = [
    lengthAtLeast(
      VALIDATION_RULES.PASSWORD.min,
      `비밀번호는 ${VALIDATION_RULES.PASSWORD.min}자 이상으로 작성해주세요`
    ),
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
  const rules = [
    lengthBetween(
      VALIDATION_RULES.NAME.min,
      VALIDATION_RULES.NAME.max,
      `이름은 ${VALIDATION_RULES.NAME.min}자 이상 ${VALIDATION_RULES.NAME.max}글자 이하로 작성해주세요`
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
