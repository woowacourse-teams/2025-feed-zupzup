import QRModal from '@/domains/admin/components/QRModal/QRModal';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof QRModal> = {
  title: 'components/QRModal',
  component: QRModal,
  tags: ['autodocs'],
  argTypes: {},
  decorators: [
    (Story) => (
      <div style={{ maxWidth: '200px', height: '80vh' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof QRModal>;

export const Default: Story = {
  render: () => {
    return <QRModal onClose={() => {}} />;
  },
};
