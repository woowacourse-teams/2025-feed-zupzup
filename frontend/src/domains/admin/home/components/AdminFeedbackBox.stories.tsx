import type { Meta, StoryObj } from '@storybook/react-webpack5';
import AdminFeedbackBox from './AdminFeedbackBox';

const meta: Meta<typeof AdminFeedbackBox> = {
  title: 'Admin/AdminFeedbackBox',
  component: AdminFeedbackBox,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    feedbackId: {
      control: { type: 'number' },
      description: '피드백 ID (고유값)',
    },
    likeCount: {
      control: { type: 'number' },
      description: '좋아요 개수',
    },
    type: {
      control: 'select',
      options: ['WAITING', 'CONFIRMED'],
      description: '피드백 상태 (대기중/확인됨)',
    },
    content: {
      control: 'text',
      description: '피드백 내용',
    },
    isSecret: {
      control: 'boolean',
      description: '비밀 피드백 여부',
    },
    imageUrl: {
      control: 'text',
      description: '피드백에 첨부된 이미지 URL',
    },
    createdAt: {
      control: 'date',
      description: '작성일',
    },
    userName: {
      control: 'text',
      description: '작성자 닉네임',
    },
    onConfirm: {
      action: 'onConfirm',
      description: '피드백 확인 처리 핸들러',
    },
    onDelete: {
      action: 'onDelete',
      description: '피드백 삭제 핸들러',
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
    feedbackId: 1,
    likeCount: 5,
    type: 'WAITING',
    content: '이 피드백은 아직 확인되지 않았습니다.',
    isSecret: false,
    imageUrl: '',
    createdAt: new Date().toISOString(),
    userName: 'Yeongi',
    onConfirm: (id: number) => console.log('Confirm', id),
    onDelete: (id: number) => console.log('Delete', id),
  },
};

export const Complete: Story = {
  args: {
    feedbackId: 2,
    likeCount: 12,
    type: 'CONFIRMED',
    content: '이 피드백은 확인되었습니다.',
    isSecret: true,
    imageUrl: 'https://via.placeholder.com/300x200',
    createdAt: new Date().toISOString(),
    userName: '관리자',
    onConfirm: (id: number) => console.log('Confirm', id),
    onDelete: (id: number) => console.log('Delete', id),
  },
};
