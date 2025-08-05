import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';
import BasicTextArea, { BasicTextAreaProps } from './BasicTextArea';

const meta: Meta<typeof BasicTextArea> = {
  title: 'Components/BasicTextArea',
  component: BasicTextArea,
  tags: ['autodocs'],
  args: {
    placeholder: '내용을 입력하세요...',
    maxLength: 300,
    minLength: 10,
    showCharCount: true,
  },
};

export default meta;

type Story = StoryObj<typeof BasicTextArea>;

const Template = (args: BasicTextAreaProps) => {
  const [value, setValue] = useState('');
  return (
    <BasicTextArea
      {...args}
      value={value}
      onChange={(e) => setValue(e.target.value)}
    />
  );
};

export const Default: Story = {
  render: Template,
};

export const WithoutCharCount: Story = {
  render: Template,
  args: {
    showCharCount: false,
    placeholder: '문자 수 표시 없이',
  },
};

export const CustomMaxLength: Story = {
  render: Template,
  args: {
    maxLength: 100,
    placeholder: '최대 100자까지 입력 가능',
  },
};
