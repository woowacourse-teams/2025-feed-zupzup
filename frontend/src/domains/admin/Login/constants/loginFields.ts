export type LoginFieldName = 'id' | 'password';

export interface LoginField {
  name: LoginFieldName;
  type: 'text' | 'password';
  labelKey: string;
  placeholder: string;
  maxLength: number;
  minLength: number;
}

export const loginFields: LoginField[] = [
  {
    name: 'id',
    type: 'text',
    labelKey: '아이디',
    placeholder: '아이디를 입력해주세요',
    maxLength: 10,
    minLength: 5,
  },
  {
    name: 'password',
    type: 'password',
    labelKey: '비밀번호',
    placeholder: '비밀번호를 입력해주세요',
    maxLength: 100,
    minLength: 5,
  },
];
