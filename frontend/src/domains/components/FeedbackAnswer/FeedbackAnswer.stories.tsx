// AlertModal.stories.tsx

import FeedbackAnswer from '@/domains/components/FeedbackAnswer/FeedbackAnswer';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof FeedbackAnswer> = {
  title: 'components/FeedbackAnswer',
  component: FeedbackAnswer,
  tags: ['autodocs'],
  argTypes: {
    answer: { control: 'text' },
  },
  args: {
    answer:
      '좋은 지적 감사합니다. 도서관 시설팀과 상의하여 의자 교체를 검토해보겠습니다.',
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

type Story = StoryObj<typeof FeedbackAnswer>;

export const Default: Story = {};
