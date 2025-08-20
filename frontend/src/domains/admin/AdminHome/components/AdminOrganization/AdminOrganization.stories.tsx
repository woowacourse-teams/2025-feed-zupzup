import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FeedbackRoom from '@/domains/admin/AdminHome/components/AdminOrganization/AdminOrganization';

const meta: Meta<typeof FeedbackRoom> = {
  title: 'components/FeedbackRoom',
  component: FeedbackRoom,
  tags: ['autodocs'],
  argTypes: {
    organizationName: { control: 'text', description: '피드백 방 이름' },
    waitingCount: {
      control: { type: 'number', min: 0 },
      description: '미처리 건 수',
    },
    postedAt: {
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
    organizationName: '회의실 A',
    waitingCount: 3,
    postedAt: new Date().toISOString(),
    onClick: () => {},
  },
};

export const NoPending: Story = {
  args: {
    organizationName: '회의실 B',
    waitingCount: 0,
    postedAt: new Date().toISOString(),
    onClick: () => {},
  },
};

export const RecentlyUpdated: Story = {
  args: {
    organizationName: '회의실 C',
    waitingCount: 8,
    postedAt: new Date(Date.now() - 3600000).toISOString(),
    onClick: () => {},
  },
};

export const LongTimeAgo: Story = {
  args: {
    organizationName: '회의실 D',
    waitingCount: 15,
    postedAt: new Date(Date.now() - 86400000).toISOString(),
    onClick: () => {},
  },
};
