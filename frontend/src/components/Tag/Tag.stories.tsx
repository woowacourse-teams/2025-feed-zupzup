import type { Meta, StoryObj } from '@storybook/react-webpack5';
import Tag from './Tag';
import { css } from '@emotion/react';

const meta: Meta<typeof Tag> = {
  title: 'Components/Tag',
  component: Tag,
  tags: ['autodocs'],
  args: {
    children: '기본 태그',
  },
  argTypes: {
    children: { control: 'text' },
    customCSS: { control: false },
  },
};

export default meta;

type Story = StoryObj<typeof Tag>;

export const Default: Story = {
  args: {
    children: '태그 예시',
  },
};

export const CustomStyle: Story = {
  args: {
    children: '커스텀 스타일 태그',
    customCSS: css`
      padding: 4px 12px;
      font-weight: bold;
      color: #0070f3;
      background-color: #f0f8ff;
      border-radius: 8px;
    `,
  },
};
