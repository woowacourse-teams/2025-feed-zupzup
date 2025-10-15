import type { Meta, StoryObj } from '@storybook/react-webpack5';
import AISummaryModal from './AISummaryModal';
import { AISummaryCategory } from '@/types/ai.types';

const meta: Meta<typeof AISummaryModal> = {
  title: 'Admin/AISummaryModal',
  component: AISummaryModal,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
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
    onClose: () => console.log('Modal closed'),
    onCategorySelect: (category: AISummaryCategory) => {
      console.log(category);
    },
  },
};
