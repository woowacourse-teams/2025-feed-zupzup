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
      captionSmall: `
        font-size: 10px;
        line-height: 14px;
        font-weight: 400;
      `,
    },
  },
  colors: {
    white: { 100: '#FFFFFF', 300: '#F9FAFB' },
    gray: { 100: '#E5E7EB', 500: '#6B7280' },
    black: { 100: '#111827' },
    red: { 100: '#EF4444' },
  },
} as unknown as Theme;

const meta: Meta<typeof FormField> = {
  title: 'Common/FormField',
  component: FormField,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
  decorators: [
    (Story) => (
      <ThemeProvider theme={mockTheme}>
        <div style={{ width: '320px' }}>
          <Story />
        </div>
      </ThemeProvider>
    ),
  ],
  argTypes: {
    label: { control: 'text', description: '라벨 텍스트' },
    placeholder: { control: 'text', description: '입력 필드 placeholder' },
    value: { control: 'text', description: '입력 값' },
    errorMessage: { control: 'text', description: '에러 메시지' },
    type: {
      control: 'select',
      options: ['text', 'password'],
      description: 'input type',
    },
  },
};
export default meta;

type Story = StoryObj<typeof FormField>;

const Controlled =
  (initialValue = '') =>
  (args: React.ComponentProps<typeof FormField>) => {
    const [value, setValue] = useState(initialValue);
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
    id: 'username',
    label: '아이디',
    type: 'text',
    placeholder: '아이디를 입력해주세요',
    maxLength: 20,
    minLength: 4,
    errorMessage: '',
  },
  render: Controlled(''),
};

export const WithError: Story = {
  args: {
    id: 'username',
    label: '아이디',
    type: 'text',
    placeholder: '아이디를 입력해주세요',
    maxLength: 20,
    minLength: 4,
    errorMessage: '아이디는 4자 이상이어야 합니다.',
  },
  render: Controlled('ab'),
};

export const PrefilledValue: Story = {
  args: {
    id: 'username',
    label: '아이디',
    type: 'text',
    placeholder: '아이디를 입력해주세요',
    maxLength: 20,
    minLength: 4,
    errorMessage: '',
  },
  render: Controlled('prefilled'),
};

export const PasswordField: Story = {
  args: {
    id: 'password',
    label: '비밀번호',
    type: 'password',
    placeholder: '비밀번호를 입력해주세요',
    maxLength: 100,
    minLength: 6,
    errorMessage: '',
  },
  render: Controlled(''),
};
