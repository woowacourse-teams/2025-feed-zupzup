import type { Meta, StoryObj } from '@storybook/react-webpack5';
import FeedbackImage from './FeedbackImage';

const meta: Meta<typeof FeedbackImage> = {
  title: 'Common/FeedbackImage',
  component: FeedbackImage,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    src: {
      control: 'text',
      description: '이미지 URL',
    },
    alt: {
      control: 'text',
      description: '이미지 대체 텍스트',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    src: 'https://i.pinimg.com/236x/44/f9/83/44f9831be884e4c65f167b96e16fa94e.jpg',
    alt: '샘플 이미지',
  },
};

export const NoAlt: Story = {
  args: {
    src: 'https://i.pinimg.com/236x/44/f9/83/44f9831be884e4c65f167b96e16fa94e.jpg',
  },
};
