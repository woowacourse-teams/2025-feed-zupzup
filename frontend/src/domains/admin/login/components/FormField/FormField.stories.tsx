import React, { useState } from 'react';
import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { ThemeProvider } from '@emotion/react';

import FormField from './FormField';
import type { Theme } from '@/theme';

const mockTheme: Theme = {
  typography: {
    pretendard: {
      caption: `
        font-size: 12px;
        line-height: 16px;
        font-weight: 400;
      `,
    },
  },
  colors: {
    white: { 300: '#FFFFFF' },
    gray: { 100: '#E5E7EB', 500: '#6B7280' },
    red: { 100: '#EF4444' },
  },
} as unknown as Theme;

const meta = {
  title: 'Commons/FormField',
  component: FormField,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
  argTypes: {
    label: {
      control: 'text',
      description: '라벨 텍스트',
      table: { type: { summary: 'string' } },
      defaultValue: '아이디',
    },
    maxLength: {
      control: 'number',
      description: '최대 길이',
      table: { type: { summary: 'number' } },
      defaultValue: 20,
    },
    minLength: {
      control: 'number',
      description: '최소 길이',
      table: { type: { summary: 'number' } },
      defaultValue: 4,
    },
  },
  decorators: [
    (Story) => (
      <ThemeProvider theme={mockTheme}>
        <div style={{ width: 360 }}>
          <Story />
        </div>
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof FormField>;

export default meta;
type Story = StoryObj<typeof meta>;

// 공통 길이 검증기
const makeLengthValidator = (min: number, max: number) => (value: string) => {
  if (value.length < min) {
    return { ok: false as const, message: `최소 ${min}자 이상 입력해주세요.` };
  }
  if (value.length > max) {
    return {
      ok: false as const,
      message: `최대 ${max}자 이하로 입력해주세요.`,
    };
  }
  return { ok: true as const };
};

// Controlled 렌더러
const Controlled =
  (initial = '') =>
  (args: React.ComponentProps<typeof FormField>) => {
    const [value, setValue] = useState(initial);
    return (
      <FormField
        {...args}
        value={value}
        onChange={(e) => setValue(e.target.value)}
      />
    );
  };

export const Default: Story = {
  args: {
    label: '아이디',
    maxLength: 20,
    minLength: 4,
    validator: makeLengthValidator(4, 20),
  },
  render: Controlled(''),
};

export const InvalidShort: Story = {
  args: {
    label: '아이디',
    maxLength: 20,
    minLength: 4,
    validator: makeLengthValidator(4, 20),
    value: '',
    onChange: () => {},
  },
  render: Controlled('abc'), // 3자 → invalid (red border)
};

export const InvalidLong: Story = {
  args: {
    label: '아이디',
    maxLength: 20,
    minLength: 4,
    validator: makeLengthValidator(4, 20),
    value: '',
    onChange: () => {},
  },
  render: Controlled('x'.repeat(21)), // 21자 → invalid
};

export const WithPrefilledValue: Story = {
  args: {
    label: '아이디',
    maxLength: 20,
    minLength: 4,
    validator: makeLengthValidator(4, 20),
    value: '',
    onChange: () => {},
  },
  render: Controlled('prefill'),
};

export const LongLabel: Story = {
  args: {
    label: '영문과 숫자를 포함한 아이디를 입력해주세요',
    maxLength: 20,
    minLength: 4,
    validator: makeLengthValidator(4, 20),
    value: '',
    onChange: () => {},
  },
  render: Controlled(''),
};
