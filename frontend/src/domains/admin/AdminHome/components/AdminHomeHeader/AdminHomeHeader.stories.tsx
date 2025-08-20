import type { Meta, StoryObj } from '@storybook/react-webpack5';
import AdminHomeHeader from './AdminHomeHeader';

const meta: Meta<typeof AdminHomeHeader> = {
  title: 'components/AdminHomeHeader',
  component: AdminHomeHeader,
  tags: ['autodocs'],
  argTypes: {
    adminName: { control: 'text', description: '관리자 이름' },
    completedCount: {
      control: { type: 'number', min: 0 },
      description: '완료된 피드백 개수',
    },
    totalCount: {
      control: { type: 'number', min: 1 },
      description: '총 피드백 개수',
    },
  },
  decorators: [
    (Story) => (
      <div
        style={{
          maxWidth: '600px',
          padding: '20px',
          backgroundColor: '#f5f5f5',
        }}
      >
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof AdminHomeHeader>;

export const Default: Story = {
  args: {
    adminName: '관리자1',
    completedCount: 17,
    totalCount: 29,
  },
};

export const AllCompleted: Story = {
  args: {
    adminName: '관리자2',
    completedCount: 50,
    totalCount: 50,
  },
};

export const NoneCompleted: Story = {
  args: {
    adminName: '관리자3',
    completedCount: 0,
    totalCount: 25,
  },
};

export const HalfCompleted: Story = {
  args: {
    adminName: '관리자4',
    completedCount: 10,
    totalCount: 20,
  },
};
