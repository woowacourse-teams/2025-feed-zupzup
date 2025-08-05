import type { Meta, StoryObj } from '@storybook/react-webpack5';
import DashboardPanel, { DashboardPanelProps } from './DashboardPanel';
import { useState } from 'react';

const meta: Meta<typeof DashboardPanel> = {
  title: 'Components/DashboardPanel',
  component: DashboardPanel,
  tags: ['autodocs'],
  args: {
    title: '활동 중인 단체 수',
    content: '24개',
    caption: '최근 1주일 기준',
    color: '#3C82F9',
    isClick: false,
    isButton: false,
  },
  argTypes: {
    title: { control: 'text' },
    content: { control: 'text' },
    caption: { control: 'text' },
    color: { control: 'color' },
    isClick: { control: 'boolean' },
    isButton: { control: 'boolean' },
    onClick: { action: 'clicked' },
  },
};

export default meta;

type Story = StoryObj<typeof DashboardPanel>;

// ✅ 인터랙티브한 버튼 스타일 스토리
const ClickableTemplate = (args: DashboardPanelProps) => {
  const [isClick, setIsClick] = useState(false);

  return (
    <DashboardPanel
      {...args}
      isClick={isClick}
      onClick={() => setIsClick((prev) => !prev)}
    />
  );
};

export const Default: Story = {
  args: {
    title: '총 후원자 수',
    content: '1,024명',
    caption: '8월 1주차 기준',
    color: '#FF6B6B',
    isButton: false,
  },
};

export const WithButton: Story = {
  render: ClickableTemplate,
  args: {
    title: '응원 총합',
    content: '3,240회',
    caption: '금주 기준',
    color: '#00C48C',
    isButton: true,
  },
};

export const CustomColor: Story = {
  args: {
    title: '이번 주 신규 단체',
    content: '3개',
    caption: '금주 집계',
    color: '#FFA500',
    isClick: true,
    isButton: false,
  },
};
