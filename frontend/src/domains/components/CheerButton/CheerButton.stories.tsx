import { useState } from 'react';
import type { Meta, StoryObj } from '@storybook/react-webpack5';
import CheerButton, { CheerButtonProps } from './CheerButton';

const meta: Meta<typeof CheerButton> = {
  title: 'Components/CheerButton',
  component: CheerButton,
  tags: ['autodocs'],
  args: {
    totalCheeringCount: 100,
  },
};

export default meta;
type Story = StoryObj<typeof CheerButton>;

// 클릭 시 로컬 상태로 accCount 증가
const Template = (args: CheerButtonProps) => {
  const [animate, setAnimate] = useState(false);

  const handleClick = () => {
    setAnimate(false);
    requestAnimationFrame(() => setAnimate(true));
  };

  return <CheerButton {...args} onClick={handleClick} animate={animate} />;
};

export const Default: Story = {
  render: Template,
  args: {
    totalCheeringCount: 120,
  },
};
