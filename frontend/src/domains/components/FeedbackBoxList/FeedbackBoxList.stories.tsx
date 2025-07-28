//@ts-expect-error: react error
import React from 'react';

import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FeedbackBoxList from './FeedbackBoxList';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';
import AdminFeedbackBox from '@/domains/admin/home/components/AdminFeedbackBox';

const meta: Meta<typeof FeedbackBoxList> = {
  title: 'Common/FeedbackBoxList',
  component: FeedbackBoxList,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    children: {
      control: false,
      description: '리스트에 들어갈 피드백 박스들',
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

const userFeedbackWaiting = {
  feedbackId: 1,
  onConfirm: () => {},
  onDelete: () => {},
  likeCount: 5,
  type: 'WAITING',
  content: '이 피드백은 아직 확인 전입니다. 곧 답변 드릴게요!',
  isLiked: false,
  isSecret: false,
  createdAt: '2024-05-20',
  customCSS: null,
} as const;

const userFeedbackConfirmed = {
  feedbackId: 2,
  onConfirm: () => {},
  onDelete: () => {},
  likeCount: 5,
  type: 'CONFIRMED',
  content: '이 피드백은 확인 완료되었습니다. 감사합니다.',
  isLiked: true,
  isSecret: false,
  createdAt: '2024-05-19',
  customCSS: null,
} as const;

const adminFeedbackWaiting = {
  feedbackId: 1,
  onConfirm: () => {},
  onDelete: () => {},
  type: 'WAITING',
  content: '관리자가 확인해야 할 피드백입니다. 이미지가 첨부되었습니다.',
  isSecret: false,
  imageUrl:
    'https://i.pinimg.com/236x/44/f9/83/44f9831be884e4c65f167b96e16fa94e.jpg',
  likeCount: 5,
  userName: '익명의 사용자',
  createdAt: '2024-05-21',
} as const;

const adminFeedbackConfirmed = {
  feedbackId: 2,
  onConfirm: () => {},
  onDelete: () => {},

  type: 'CONFIRMED',
  content: '관리자가 확인 완료한 피드백입니다. 비밀글입니다.',
  isSecret: true,
  imageUrl: null,
  likeCount: 12,
  userName: '김개발',
  createdAt: '2024-05-18',
} as const;

export const Default: Story = {
  args: {
    children: (
      <>
        <UserFeedbackBox {...userFeedbackWaiting} />
        <UserFeedbackBox {...userFeedbackConfirmed} />
        <UserFeedbackBox {...userFeedbackWaiting} isSecret />
      </>
    ),
  },
};

export const Admin: Story = {
  args: {
    children: (
      <>
        <AdminFeedbackBox {...adminFeedbackWaiting} />
        <AdminFeedbackBox {...adminFeedbackConfirmed} />
      </>
    ),
  },
};
