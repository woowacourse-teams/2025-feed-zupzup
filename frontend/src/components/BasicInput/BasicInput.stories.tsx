import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';
import BasicInput, { BasicInputProps } from './BasicInput';

const meta: Meta<typeof BasicInput> = {
  title: 'Components/BasicInput',
  component: BasicInput,
  tags: ['autodocs'],
  args: {
    placeholder: '이름을 입력하세요',
    maxLength: 20,
    minLength: 1,
    showCharCount: true,
  },
};

export default meta;

type Story = StoryObj<typeof BasicInput>;

const Template = (args: BasicInputProps) => {
  const [value, setValue] = useState('');

  return (
    <BasicInput
      {...args}
      value={value}
      onChange={(e) => setValue(e.target.value)}
    />
  );
};

export const Default: Story = {
  render: Template,
  args: {
    placeholder: '기본 입력창',
  },
};

export const WithCharCount: Story = {
  render: Template,
  args: {
    placeholder: '문자 수 표시',
    showCharCount: true,
    maxLength: 30,
  },
};

export const WithoutCharCount: Story = {
  render: Template,
  args: {
    placeholder: '문자 수 미표시',
    showCharCount: false,
  },
};

export const CustomMaxLength: Story = {
  render: Template,
  args: {
    placeholder: '최대 5자 입력',
    maxLength: 5,
  },
};
