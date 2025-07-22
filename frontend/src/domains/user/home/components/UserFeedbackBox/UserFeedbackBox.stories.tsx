import type { Meta, StoryObj } from '@storybook/react-webpack5';
import UserFeedbackBox from './UserFeedbackBox';

const meta: Meta<typeof UserFeedbackBox> = {
  title: 'User/UserFeedbackBox',
  component: UserFeedbackBox,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: 'select',
      options: ['WAITING', 'CONFIRMED'],
      description: '상태에 따라 스타일이 달라집니다.',
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

export const Incomplete: Story = {
  args: {
    type: 'WAITING',
  },
};

export const Complete: Story = {
  args: {
    type: 'CONFIRMED',
  },
};
