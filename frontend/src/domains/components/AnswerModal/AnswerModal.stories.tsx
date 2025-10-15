import AnswerModal from '@/domains/components/AnswerModal/AnswerModal';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof AnswerModal> = {
  title: 'components/AnswerModal',
  component: AnswerModal,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <div style={{ maxWidth: '380px', height: '50vh' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof AnswerModal>;

export const Default: Story = {
  render: () => {
    return (
      <div>
        <AnswerModal handleCloseModal={() => {}} handleSubmit={() => {}} />
      </div>
    );
  },
};
