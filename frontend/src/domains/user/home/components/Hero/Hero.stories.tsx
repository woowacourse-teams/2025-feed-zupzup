import type { Meta, StoryObj } from '@storybook/react-webpack5';
import Hero from './Hero';

const meta: Meta<typeof Hero> = {
  title: 'user/Hero',
  component: Hero,
  tags: ['autodocs'],
  argTypes: {
    onLoginClick: { action: '로그인 클릭' },
    onSuggestClick: { action: '건의하기 클릭' },
    title: { control: 'text' },
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

type Story = StoryObj<typeof Hero>;

export const Default: Story = {
  args: {
    title: '카페',
  },
};
