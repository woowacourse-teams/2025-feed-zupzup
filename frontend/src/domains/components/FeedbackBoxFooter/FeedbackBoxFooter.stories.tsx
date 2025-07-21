import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FeedbackBoxFooter from './FeedbackBoxFooter';

const meta: Meta<typeof FeedbackBoxFooter> = {
  title: 'Common/FeedbackBoxFooter',
  component: FeedbackBoxFooter,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    likeCount: {
      control: 'number',
      description: '좋아요 개수',
    },
  },
  decorators: [
    (Story) => (
      <div style={{ width: '500px' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {},
};

export const WithLikes: Story = {
  args: {
    likeCount: 10,
  },
};
