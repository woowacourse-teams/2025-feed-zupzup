import EditRoomModal from '@/domains/admin/EditRoomModal/EditRoomModal';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof EditRoomModal> = {
  title: 'components/EditRoomModal',
  component: EditRoomModal,
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

type Story = StoryObj<typeof EditRoomModal>;

export const Default: Story = {
  render: () => {
    return <EditRoomModal onClose={() => {}} />;
  },
};
