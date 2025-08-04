import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';
import { css } from '@emotion/react';
import TimeDelayModal, { TimeDelayModalProps } from './TimeDelayModal';

const meta: Meta<typeof TimeDelayModal> = {
  title: 'Components/TimeDelayModal',
  component: TimeDelayModal,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    isOpen: {
      control: 'boolean',
      description: '모달의 열림/닫힘 상태',
    },
    loadingDuration: {
      control: { type: 'number', min: 500, max: 5000, step: 500 },
      description: '로딩 상태 지속 시간 (ms)',
    },
    autoCloseDuration: {
      control: { type: 'number', min: 1000, max: 10000, step: 500 },
      description: '완료 후 자동 닫힘까지의 시간 (ms)',
    },
    loadingMessage: {
      control: 'text',
      description: '로딩 상태에서 보여줄 메시지',
    },
    completeMessage: {
      control: 'text',
      description: '완료 상태에서 보여줄 메시지',
    },
    width: {
      control: { type: 'number', min: 200, max: 600, step: 50 },
      description: '모달의 너비',
    },
    height: {
      control: { type: 'number', min: 100, max: 400, step: 50 },
      description: '모달의 높이',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// 기본 스토리를 위한 래퍼 컴포넌트
const ModalWrapper = (args: TimeDelayModalProps) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div>
      <button
        onClick={() => setIsOpen(true)}
        style={{
          padding: '12px 24px',
          backgroundColor: '#007bff',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
        }}
      >
        모달 열기
      </button>
      <TimeDelayModal
        {...args}
        isOpen={isOpen}
        onClose={() => {
          setIsOpen(false);
        }}
      />
    </div>
  );
};

export const Default: Story = {
  render: (args) => <ModalWrapper {...args} />,
  args: {
    loadingDuration: 2000,
    autoCloseDuration: 2000,
    loadingMessage: '피드백을 전송하고 있어요...',
    completeMessage: '소중한 의견 감사해요!',
    width: 300,
  },
};

export const FastProcess: Story = {
  render: (args) => <ModalWrapper {...args} />,
  args: {
    loadingDuration: 1000,
    autoCloseDuration: 1500,
    loadingMessage: '빠른 처리 중...',
    completeMessage: '완료!',
    width: 300,
  },
};

export const WithCustomCSS: Story = {
  render: (args) => <ModalWrapper {...args} />,
  args: {
    loadingDuration: 2000,
    autoCloseDuration: 2000,
    loadingMessage: '스타일이 적용된 모달이에요',
    completeMessage: '완료되었습니다!',
    width: 350,
    customCSS: css`
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 20px;
      box-shadow: 0 20px 40px rgb(0 0 0 / 30%);
    `,
  },
};

export const LoadingState: Story = {
  args: {
    isOpen: true,
    onClose: () => {},
    loadingDuration: 999999999,
    loadingMessage: '로딩 상태입니다...',
    width: 300,
  },
};

export const CompleteState: Story = {
  args: {
    isOpen: true,
    onClose: () => {},
    loadingDuration: 0,
    autoCloseDuration: 999999999,
    completeMessage: '완료 상태입니다!',
    width: 300,
  },
};
