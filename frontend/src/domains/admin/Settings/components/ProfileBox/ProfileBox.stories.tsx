import type { Meta, StoryObj } from '@storybook/react-webpack5';
import ProfileBox from './ProfileBox';

const meta: Meta<typeof ProfileBox> = {
  title: 'Admin/Settings/ProfileBox',
  component: ProfileBox,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    name: {
      control: 'text',
      description: '관리자 이름',
    },
    id: {
      control: 'text',
      description: '관리자 ID',
    },
  },
};

export default meta;
type Story = StoryObj<typeof ProfileBox>;

export const Default: Story = {
  args: {
    name: '김관리자',
    id: 'admin123',
  },
};

export const LongName: Story = {
  args: {
    name: '매우긴관리자이름입니다',
    id: 'verylongadminid123',
  },
};

export const ShortName: Story = {
  args: {
    name: '김',
    id: 'a',
  },
};
