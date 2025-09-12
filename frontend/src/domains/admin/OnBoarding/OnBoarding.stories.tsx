import OnBoarding from './OnBoarding';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof OnBoarding> = {
  title: 'components/OnBoarding',
  component: OnBoarding,
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

type Story = StoryObj<typeof OnBoarding>;

export const Default: Story = {
  render: () => {
    return <OnBoarding />;
  },
};
