import { ArrowIcon } from '@/components/icons/arrowIcon';
import { css } from '@emotion/react';
import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FloatingButton, { Position } from './FloatingButton';

// ✅ 메타 정보
const meta: Meta<typeof FloatingButton> = {
  title: 'Components/FloatingButton',
  component: FloatingButton,
  tags: ['autodocs'],
  args: {
    icon: <ArrowIcon />,
    inset: {
      bottom: '24px',
      right: '24px',
    },
    customCSS: css`
      width: 60px;
      height: 60px;
      background-color: #9c6bff;
      border-radius: 30px;
      box-shadow: 0 4px 10px rgb(0 0 0 / 15%);
    `,
    onClick: () => alert('버튼 클릭!'),
  },
};

export default meta;
type Story = StoryObj<typeof FloatingButton>;

// ✅ 기본 버튼
export const Default: Story = {};

// ✅ 오른쪽 위
export const Right: Story = {
  args: {
    inset: {
      left: '100%',
    } as Position,
    customCSS: css`
      width: 60px;
      height: 60px;
      background-color: #9c6bff;
      border-radius: 30px;
      box-shadow: 0 4px 10px rgb(0 0 0 / 15%);
    `,
  },
};

// ✅ 커스텀 스타일 적용 (크기 및 배경색 변경 등)
export const WithCustomStyle: Story = {
  args: {
    customCSS: css`
      width: 40px;
      height: 40px;
      background-color: #ff6b6b;
      border-radius: 30px;
      box-shadow: 0 4px 10px rgb(0 0 0 / 15%);
    `,
  },
};
