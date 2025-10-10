import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';
import AISummary from './AISummary';

const meta: Meta<typeof AISummary> = {
  title: 'Admin/AISummary',
  component: AISummary,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    isOpen: {
      control: 'boolean',
      description: '모달 열림/닫힘 상태',
    },
    onClose: {
      action: 'onClose',
      description: '모달 닫기 콜백',
    },
    onCategorySelect: {
      action: 'onCategorySelect',
      description: '카테고리 선택 콜백',
    },
  },
  decorators: [
    (Story) => (
      <div style={{ width: '100vw', height: '100vh' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;
type Story = StoryObj<typeof meta>;

// 기본 스토리
export const Default: Story = {
  args: {
    isOpen: true,
    onClose: () => console.log('Modal closed'),
    onCategorySelect: (category: string) =>
      console.log('Category selected:', category),
  },
};

// 모달이 닫힌 상태
export const Closed: Story = {
  args: {
    isOpen: false,
    onClose: () => console.log('Modal closed'),
    onCategorySelect: (category: string) =>
      console.log('Category selected:', category),
  },
};

// 인터랙티브 예제
export const Interactive: Story = {
  render: () => {
    const [isOpen, setIsOpen] = useState(false);

    return (
      <div style={{ padding: '20px' }}>
        <button
          onClick={() => setIsOpen(true)}
          style={{
            padding: '10px 20px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          AI 요약 열기
        </button>
        <AISummary
          isOpen={isOpen}
          onClose={() => setIsOpen(false)}
          onCategorySelect={(category) => {
            console.log('선택된 카테고리:', category);
            setIsOpen(false);
          }}
        />
      </div>
    );
  },
};
