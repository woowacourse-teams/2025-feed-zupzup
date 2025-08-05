import { ArrowIcon } from '@/components/icons/arrowIcon';
import { css } from '@emotion/react';
import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FloatingButton, { Position } from './FloatingButton';

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
  argTypes: {
    icon: { control: false },
    inset: { control: 'object' },
    customCSS: { control: false },
    onClick: { action: 'clicked' },
  },
};

export default meta;
type Story = StoryObj<typeof FloatingButton>;

export const Default: Story = {};

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
