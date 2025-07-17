import { Meta, StoryObj } from '@storybook/react-webpack5';
import Banner from './Banner';
import { BannerProps } from './Banner.type';

const meta: Meta<BannerProps> = {
  title: 'Components/Banner',
  component: Banner,
  tags: ['autodocs'],
  args: {
    title: '더 나은 서비스를 위해',
    description: '여러분의 소중한 의견이 더 좋은 카페를 만들어갑니다.',
  },
};

export default meta;

type Story = StoryObj<BannerProps>;

export const Default: Story = {};

export const LongText: Story = {
  args: {
    title:
      '여러 줄 제목 예시입니다. 매우 긴 텍스트가 들어올 경우를 가정합니다.',
    description:
      '이 설명은 일반적인 길이를 초과하며, 배너 컴포넌트가 여러 줄로 잘 동작하는지 확인하기 위해 작성된 예시입니다. 스타일이 깨지지 않아야 합니다.',
  },
};

export const CustomText: Story = {
  args: {
    title: '사용자 정의 제목',
    description: '이건 사용자 정의 설명입니다.',
  },
};
