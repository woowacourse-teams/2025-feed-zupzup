import AnswerModal from '@/domains/components/AnswerModal/AnswerModal';
import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';

const meta: Meta<typeof AnswerModal> = {
  title: 'components/AnswerModal',
  component: AnswerModal,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <div style={{ maxWidth: '380px', height: '50vh' }}>
        <Story />
      </div>
    ),
  ],
  argTypes: {
    isOpen: {
      control: 'boolean',
      description: '모달의 등장 여부를 정하는 boolean',
    },
  },
};

export default meta;

type Story = StoryObj<typeof AnswerModal>;

export const Default: Story = {
  render: () => {
    const [isOpen, setIsOpen] = useState(true);

    return (
      <AnswerModal isOpen={isOpen} handleCloseModal={() => setIsOpen(false)} />
    );
  },
};
