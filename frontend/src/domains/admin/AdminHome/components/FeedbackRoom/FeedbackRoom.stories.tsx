import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FeedbackRoom from '@/domains/admin/AdminHome/components/FeedbackRoom/FeedbackRoom';

const meta: Meta<typeof FeedbackRoom> = {
  title: 'components/FeedbackRoom',
  component: FeedbackRoom,
  tags: ['autodocs'],
  argTypes: {
    roomName: { control: 'text', description: '피드백 방 이름' },
    pendingCount: {
      control: { type: 'number', min: 0 },
      description: '미처리 건 수',
    },
    feedbackHoursAgo: {
      control: { type: 'number', min: 0 },
      description: '최근 피드백 시각(시간 전)',
    },
    onClick: {
      action: 'clicked',
      description: '컴포넌트를 클릭했을 때 실행되는 핸들러',
    },
  },
  decorators: [
    (Story) => (
      <div
        style={{
          maxWidth: '400px',
          padding: '20px',
        }}
      >
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof FeedbackRoom>;

export const Default: Story = {
  args: {
    roomName: '회의실 A',
    pendingCount: 3,
    feedbackHoursAgo: 5,
    onClick: () => {},
  },
};

export const NoPending: Story = {
  args: {
    roomName: '회의실 B',
    pendingCount: 0,
    feedbackHoursAgo: 12,
    onClick: () => {},
  },
};

export const RecentlyUpdated: Story = {
  args: {
    roomName: '회의실 C',
    pendingCount: 8,
    feedbackHoursAgo: 1,
    onClick: () => {},
  },
};

export const LongTimeAgo: Story = {
  args: {
    roomName: '회의실 D',
    pendingCount: 15,
    feedbackHoursAgo: 48,
    onClick: () => {},
  },
};
