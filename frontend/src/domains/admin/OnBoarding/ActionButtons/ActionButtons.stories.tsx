import ActionButtons from './ActionButtons';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof ActionButtons> = {
  title: 'components/ActionButtons',
  component: ActionButtons,
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

type Story = StoryObj<typeof ActionButtons>;

export const Default: Story = {
  render: () => {
    return <ActionButtons />;
  },
};
