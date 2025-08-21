import type { Meta, StoryObj } from '@storybook/react-webpack5';
import MoreMenu from '@/components/Header/MoreMenu/MoreMenu';

const meta: Meta<typeof MoreMenu> = {
  title: 'Components/MoreMenu',
  component: MoreMenu,
  tags: ['autodocs'],
  argTypes: {},
  decorators: [
    (Story) => (
      <div style={{ padding: '20px', backgroundColor: 'black' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof MoreMenu>;

export const Default: Story = {
  args: {
    closeMoreMenu: () => {},
  },
};
