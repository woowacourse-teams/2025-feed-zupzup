import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';
import RefreshButton from './RefreshButton';

// 스토리북 메타 정보
const meta: Meta<typeof RefreshButton> = {
  title: 'Components/RefreshButton',
  component: RefreshButton,
  args: {
    feedbackDiff: 0,
    handleRefresh: () => alert('새로고침 실행!'),
  },
};

export default meta;
type Story = StoryObj<typeof RefreshButton>;

/** ✅ 기본 상태 (새 피드백 없음) */
export const Default: Story = {
  args: {
    feedbackDiff: 0,
  },
  render: (args) => <RefreshButton {...args} />,
};

/** ✅ 새 피드백이 있을 때 (반짝임 + 숫자 표시) */
export const WithFeedback: Story = {
  args: {
    feedbackDiff: 5,
  },
  render: (args) => <RefreshButton {...args} />,
};

/** ✅ 클릭 시 반짝임 토글 테스트 */
export const ToggleFeedback: Story = {
  render: (args) => {
    const [diff, setDiff] = useState(0);

    return (
      <RefreshButton
        {...args}
        feedbackDiff={diff}
        handleRefresh={() => {
          setDiff((prev) => (prev > 0 ? 0 : 3)); // 0 ↔ 3 토글
          console.log('새로고침 클릭됨!');
        }}
      />
    );
  },
};
