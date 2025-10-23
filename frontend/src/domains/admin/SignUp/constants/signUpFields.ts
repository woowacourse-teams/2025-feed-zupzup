export type SignUpFieldName = 'name' | 'id' | 'password' | 'passwordConfirm';

export interface SignUpField {
  name: SignUpFieldName;
  type: 'text' | 'password';
  labelKey: string;
  placeholder: string;
  maxLength: number;
  minLength: number;
}

export const signUpFields: SignUpField[] = [
  {
    name: 'name',
    type: 'text',
    labelKey: '이름',
    placeholder: '이름을 입력해주세요',
    maxLength: 10,
    minLength: 1,
  },
  {
    name: 'id',
    type: 'text',
    labelKey: '아이디',
    placeholder: '아이디를 입력해주세요',
    maxLength: 20,
    minLength: 5,
  },
  {
    name: 'password',
    type: 'password',
    labelKey: '비밀번호',
    placeholder: '비밀번호를 5글자 이상 입력해주세요',
    maxLength: 100,
    minLength: 5,
  },
  {
    name: 'passwordConfirm',
    type: 'password',
    labelKey: '비밀번호 확인',
    placeholder: '비밀번호를 다시 입력해주세요',
    maxLength: 100,
    minLength: 5,
  },
];
