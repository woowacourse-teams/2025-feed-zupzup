//@ts-expect-error: react error

import React from 'react';
import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FeedbackBoxList from './FeedbackBoxList';
import UserFeedbackBox from '@/domains/user/home/components/UserFeedbackBox/UserFeedbackBox';
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

export const Default: Story = {
  args: {
    children: (
      <>
        <UserFeedbackBox type='incomplete' />
        <UserFeedbackBox type='incomplete' />
        <UserFeedbackBox type='complete' />
      </>
    ),
  },
};

export const Admin: Story = {
  args: {
    children: (
      <>
        <AdminFeedbackBox type='incomplete' />
        <AdminFeedbackBox type='incomplete' />
        <AdminFeedbackBox type='complete' />
      </>
    ),
  },
};
