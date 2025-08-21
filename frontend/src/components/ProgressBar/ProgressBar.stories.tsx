import type { Meta, StoryObj } from '@storybook/react-webpack5';
import ProgressBar from '@/components/ProgressBar/ProgressBar';

const meta: Meta<typeof ProgressBar> = {
  title: 'Common/ProgressBar',
  component: ProgressBar,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    totalStep: {
      control: 'number',
      description: '총 단계',
    },
    currentStep: {
      control: 'number',
      description: '현재 단계',
    },
  },

  decorators: [
    (Story) => (
      <div style={{ width: '600px' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    currentStep: 1,
    totalStep: 2,
  },
};
