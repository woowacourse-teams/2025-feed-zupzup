import ManageRoomModal from '@/domains/admin/ManageRoomModal/ManageRoomModal';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof ManageRoomModal> = {
  title: 'components/ManageRoomModal',
  component: ManageRoomModal,
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

type Story = StoryObj<typeof ManageRoomModal>;

export const Default: Story = {
  render: () => {
    return <ManageRoomModal onClose={() => {}} />;
  },
};
