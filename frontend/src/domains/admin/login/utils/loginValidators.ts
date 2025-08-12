type ValidationState = { ok: true } | { ok: false; message: string };

export const validateName = (value: string): ValidationState => {
  if (value.length < 5 || value.length > 10) {
    return {
      ok: false,
      message: '아이디는 5자 이상 10글자 이하로 작성해주세요',
    };
  }
  if (!/^[a-zA-Z0-9]+$/.test(value)) {
    return {
      ok: false,
      message: '아이디는 영문 대소문자와 숫자만 사용할 수 있습니다.',
    };
  }
  if (/\s/.test(value)) {
    return { ok: false, message: '아이디에 공백을 포함할 수 없습니다.' };
  }
  return { ok: true };
};

export const validatePassword = (value: string): ValidationState => {
  if (value.length < 5) {
    return { ok: false, message: '비밀번호는 5자 이상으로 작성헤주세요' };
  }
  if (!/[a-zA-Z]/.test(value)) {
    return {
      ok: false,
      message: '비밀번호는 영문 대소문자만 사용할 수 있습니다.',
    };
  }
  if (/\s/.test(value)) {
    return { ok: false, message: '비밀번호에 공백을 포함할 수 없습니다.' };
  }
  return { ok: true };
};
