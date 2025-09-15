import RealTimeNotificationScene from './RealTimeNotificationScene';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof RealTimeNotificationScene> = {
  title: 'components/RealTimeNotificationScene',
  component: RealTimeNotificationScene,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <div style={{ maxWidth: '600px', height: '80vh' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof RealTimeNotificationScene>;

export const Default: Story = {
  render: () => {
    return <RealTimeNotificationScene />;
  },
};
