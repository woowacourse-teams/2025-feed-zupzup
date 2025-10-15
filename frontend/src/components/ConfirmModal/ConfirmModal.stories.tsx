import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof ConfirmModal> = {
  title: 'components/ConfirmModal',
  component: ConfirmModal,
  tags: ['autodocs'],
  argTypes: {
    title: { control: 'text' },
    message: { control: 'text' },
    confirmText: { control: 'text' },
    cancelText: { control: 'text' },
    width: { control: 'number' },
    height: { control: 'number' },
  },
  decorators: [
    (Story) => (
      <div style={{ maxWidth: '600px', height: '80vh' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof ConfirmModal>;

export const Default: Story = {
  render: (args) => {
    return (
      <ConfirmModal
        {...args}
        onClose={() => {}}
        onConfirm={() => {
          alert('확인 버튼이 클릭되었습니다.');
        }}
      />
    );
  },
  args: {
    title: '정말 삭제하시겠습니까?',
    message: '삭제한 내용은 복구할 수 없습니다.',
    confirmText: '삭제',
    cancelText: '취소',
    width: 360,
  },
};
