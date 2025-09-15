import Content from './Content';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof Content> = {
  title: 'components/Content',
  component: Content,
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

type Story = StoryObj<typeof Content>;

export const Default: Story = {
  render: () => {
    return <Content />;
  },
};
