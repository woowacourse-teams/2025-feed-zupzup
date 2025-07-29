import type { Meta, StoryObj } from '@storybook/react-webpack5';
import OnBoarding from '@/domains/components/OnBoarding/OnBoarding';

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
