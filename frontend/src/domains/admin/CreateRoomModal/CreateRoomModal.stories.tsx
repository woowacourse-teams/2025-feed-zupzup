import CreateRoomModal from '@/domains/admin/CreateRoomModal/CreateRoomModal';
import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';

const meta: Meta<typeof CreateRoomModal> = {
  title: 'components/CreateRoomModal',
  component: CreateRoomModal,
  tags: ['autodocs'],
  argTypes: {
    isOpen: { control: 'boolean' },
    onClose: { action: 'closed' },
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

type Story = StoryObj<typeof CreateRoomModal>;

export const Default: Story = {
  render: () => {
    const [isOpen, setIsOpen] = useState(true);

    return <CreateRoomModal isOpen={isOpen} onClose={() => setIsOpen(false)} />;
  },
};
