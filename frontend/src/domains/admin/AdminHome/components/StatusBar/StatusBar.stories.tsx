import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';
import StatusBar from '@/domains/admin/AdminHome/components/StatusBar/StatusBar';

const meta: Meta<typeof StatusBar> = {
  title: 'components/StatusBar',
  component: StatusBar,
  tags: ['autodocs'],
  argTypes: {
    status: { control: { type: 'range', min: 0, max: 100, step: 1 } },
  },
  decorators: [
    (Story) => (
      <div
        style={{
          maxWidth: '600px',
          padding: '20px',
          backgroundColor: '#0009',
          color: '#fff',
        }}
      >
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof StatusBar>;

export const Default: Story = {
  render: () => {
    const [status, setStatus] = useState(45);
    return (
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <input
          type='range'
          min={0}
          max={100}
          step={1}
          value={status}
          onChange={(e) => setStatus(Number(e.target.value))}
        />
        <StatusBar status={status} />
      </div>
    );
  },
};

export const Zero: Story = {
  args: { status: 0 },
};

export const Half: Story = {
  args: { status: 50 },
};

export const Full: Story = {
  args: { status: 100 },
};

export const OutOfRangeLow: Story = {
  name: 'Out of Range (-20 → 0)',
  args: { status: -20 as unknown as number },
};

export const OutOfRangeHigh: Story = {
  name: 'Out of Range (150 → 100)',
  args: { status: 150 as unknown as number },
};
