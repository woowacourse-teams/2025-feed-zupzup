import avatar1 from '@/assets/images/avatar1.png';
import avatar2 from '@/assets/images/avatar2.png';
import avatar3 from '@/assets/images/avatar3.png';
import avatar4 from '@/assets/images/avatar4.png';
import avatar5 from '@/assets/images/avatar5.png';
import avatar6 from '@/assets/images/avatar6.png';
import avatar7 from '@/assets/images/avatar7.png';
import avatar8 from '@/assets/images/avatar8.png';

export const FEEDBACK_INPUT_CONSTANTS = {
  ADJECTIVES: [
    '다정한',
    '귀여운',
    '행복한',
    '활발한',
    '똑똑한',
    '용감한',
    '착한',
    '밝은',
    '재미있는',
    '따뜻한',
    '부지런한',
    '친절한',
    '상냥한',
    '시원한',
    '멋진',
    '예쁜',
    '푸른',
    '빠른',
    '느긋한',
    '신비한',
  ] as const,

  ANIMALS: [
    '고양이',
    '강아지',
    '햄스터',
    '토끼',
    '앵무새',
    '펭귄',
    '코알라',
    '판다',
    '다람쥐',
    '여우',
    '사자',
    '호랑이',
    '곰',
    '늑대',
    '독수리',
    '부엉이',
    '거북이',
    '돌고래',
    '고래',
    '기린',
  ] as const,

  AVATARS: [
    avatar1,
    avatar2,
    avatar3,
    avatar4,
    avatar5,
    avatar6,
    avatar7,
    avatar8,
  ] as const,

  DEFAULTS: {
    USERNAME: '다정한 고양이',
    AVATAR: avatar1,
    FEEDBACK: '',
    IS_LOCKED: false,
    MAX_LENGTH: 500,
    MIN_LENGTH: 1,
  } as const,

  PLACEHOLDER: '건의하고 싶은 내용을 자세히 입력해주세요',

  generateRandomUsername(): string {
    const randomAdjective =
      this.ADJECTIVES[Math.floor(Math.random() * this.ADJECTIVES.length)];
    const randomAnimal =
      this.ANIMALS[Math.floor(Math.random() * this.ANIMALS.length)];
    return `${randomAdjective} ${randomAnimal}`;
  },
} as const;
