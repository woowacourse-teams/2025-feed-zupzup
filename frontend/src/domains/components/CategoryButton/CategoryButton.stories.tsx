import type { Meta, StoryObj } from '@storybook/react-webpack5';
import CategoryButton from '@/domains/components/CategoryButton/CategoryButton';

const meta: Meta<typeof CategoryButton> = {
  title: 'components/CategoryButton',
  component: CategoryButton,
  tags: ['autodocs'],
  argTypes: {
    onClick: { action: '카테고리 버튼 클릭' },
    text: { control: 'text' },
    icon: { control: 'text' },
  },
  decorators: [
    (Story) => (
      <div style={{ minHeight: '80vh', padding: '32px' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof CategoryButton>;

export const Default: Story = {
  args: {
    text: '건의',
    onClick: () => console.log('클릭'),
    icon: '😊',
  },
};
