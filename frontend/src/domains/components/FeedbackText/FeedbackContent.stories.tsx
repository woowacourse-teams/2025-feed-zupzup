import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FeedbackText from './FeedbackContent';

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
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    text: '피드백 예시입니다.',
  },
};
