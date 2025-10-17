import type { Meta, StoryObj } from '@storybook/react-webpack5';
import Toast from './Toast';

// Storybook 메타 정보
const meta: Meta<typeof Toast> = {
  title: 'Components/Toast',
  component: Toast,
  args: {
    message: '회원가입 정보를 확인해주세요.',
    duration: 3000,
  },
};

export default meta;
type Story = StoryObj<typeof Toast>;

// 기본 토스트
export const Default: Story = {
  render: (args) => {
    return <Toast {...args} />;
  },
};

// 짧게 사라지는 토스트
export const ShortDuration: Story = {
  args: {
    message: '짧게 뜨는 토스트',
    duration: 1000,
  },
  render: (args) => {
    return <Toast {...args} />;
  },
};

// 길게 유지되는 토스트
export const LongDuration: Story = {
  args: {
    message: '길게 유지되는 토스트 (5초)',
    duration: 5000,
  },
  render: (args) => {
    return <Toast {...args} />;
  },
};
