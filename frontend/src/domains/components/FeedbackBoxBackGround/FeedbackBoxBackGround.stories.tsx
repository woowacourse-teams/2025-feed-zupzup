import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FeedbackBoxBackGround from './FeedbackBoxBackGround';

const meta: Meta<typeof FeedbackBoxBackGround> = {
  title: 'Common/FeedbackBoxBackGround',
  component: FeedbackBoxBackGround,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: 'select',
      options: ['incomplete', 'complete'],
      description: '상태에 따라 배경 스타일이 달라집니다.',
    },
    children: {
      control: false,
      description: '내부에 들어갈 컴포넌트',
    },
  },
  decorators: [
    (Story) => (
      <div style={{ width: '500px' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Incomplete: Story = {
  args: {
    type: 'incomplete',
    children: <div>미완료 상태의 배경입니다.</div>,
  },
};

export const Complete: Story = {
  args: {
    type: 'complete',
    children: <div>완료 상태의 배경입니다.</div>,
  },
};
