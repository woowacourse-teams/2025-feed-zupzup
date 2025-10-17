import CreateRoomModal from '@/domains/admin/CreateRoomModal/CreateRoomModal';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof CreateRoomModal> = {
  title: 'components/CreateRoomModal',
  component: CreateRoomModal,
  tags: ['autodocs'],
  argTypes: {
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
    return <CreateRoomModal onClose={() => {}} />;
  },
};
