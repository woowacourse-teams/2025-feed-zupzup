// AlertModal.stories.tsx

import AlertModal from '@/components/AlertModal/AlertModal';
import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';

const meta: Meta<typeof AlertModal> = {
  title: 'components/AlertModal',
  component: AlertModal,
  tags: ['autodocs'],
  argTypes: {
    title: { control: 'text' },
    message: { control: 'text' },
    confirmText: { control: 'text' },
  },
  decorators: [
    (Story) => (
      <div style={{ maxWidth: '600px', height: '80vh' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof AlertModal>;

export const Default: Story = {
  render: (args) => {
    const [isOpen, setIsOpen] = useState(true);

    return (
      <AlertModal
        {...args}
        isOpen={isOpen}
        onClose={() => setIsOpen(false)}
        onConfirm={() => {
          alert('확인 버튼이 클릭되었습니다.');
          setIsOpen(false);
        }}
      />
    );
  },
  args: {
    title: '알림',
    message: '정말로 삭제하시겠습니까?',
    confirmText: '확인',
  },
};
