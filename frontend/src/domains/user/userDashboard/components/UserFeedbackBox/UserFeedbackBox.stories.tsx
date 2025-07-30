//@ts-expect-error: react error
import React from 'react';

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
    isLiked: {
      control: 'boolean',
      description: '좋아요 여부',
    },
    isSecret: {
      control: 'boolean',
      description: '비밀 피드백 여부',
    },
    createdAt: {
      control: 'date',
      description: '작성일',
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
    content: '이 피드백은 아직 확인 전입니다. 곧 답변 드릴게요!',
    isLiked: false,
    isSecret: false,
    createdAt: '2024-05-20T123',
  },
};

export const Complete: Story = {
  args: {
    feedbackId: 1,
    likeCount: 5,
    type: 'CONFIRMED',
    content: '이 피드백은 아직 확인 전입니다. 곧 답변 드릴게요!',
    isLiked: false,
    isSecret: false,
    createdAt: '2024-05-20T123',
  },
};
