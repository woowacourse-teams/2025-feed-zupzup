import OnBoarding from '@/domains/user/OnBoarding/OnBoarding';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof OnBoarding> = {
  title: 'component/OnBoarding',
  component: OnBoarding,
  tags: ['autodocs'],
  argTypes: {},
  decorators: [
    (Story) => (
      <div style={{ maxWidth: '600px', height: '80vh' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof OnBoarding>;

export const Default: Story = {};
