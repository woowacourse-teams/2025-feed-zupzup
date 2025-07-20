import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FeedbackText from './FeedbackText';

const meta: Meta<typeof FeedbackText> = {
  title: 'Common/FeedbackText',
  component: FeedbackText,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    text: {
      control: 'text',
      description: '피드백 텍스트',
    },
    type: {
      control: 'select',
      options: ['incomplete', 'complete'],
      description: '상태에 따라 텍스트 스타일이 달라집니다.',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    text: '피드백 예시입니다.',
    type: 'incomplete',
  },
};

export const Complete: Story = {
  args: {
    text: '완료된 피드백입니다.',
    type: 'complete',
  },
};
