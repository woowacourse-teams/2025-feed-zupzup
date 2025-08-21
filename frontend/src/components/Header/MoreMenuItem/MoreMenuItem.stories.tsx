import type { Meta, StoryObj } from '@storybook/react-webpack5';
import MoreMenuItem from '@/components/Header/MoreMenuItem/MoreMenuItem';

const meta: Meta<typeof MoreMenuItem> = {
  title: 'Components/MoreMenuItem',
  component: MoreMenuItem,
  tags: ['autodocs'],
  argTypes: {
    icon: { control: 'text' }, // Assuming icon is a string or React node
    menu: { control: 'text' },
  },
};

export default meta;

type Story = StoryObj<typeof MoreMenuItem>;

export const Default: Story = {
  args: {
    icon: '👍',
    menu: '기본 메뉴',
  },
};
