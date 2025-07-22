import type { Meta, StoryObj } from '@storybook/react-webpack5';
import CategorySelector from './CategorySelector';

const options = [
  { value: 'cat', label: '고양이' },
  { value: 'dog', label: '강아지' },
  { value: 'bird', label: '새', disabled: true },
  { value: 'fish', label: '물고기' },
];

const meta: Meta<typeof CategorySelector> = {
  title: 'components/CategorySelector',
  component: CategorySelector,
  tags: ['autodocs'],
  argTypes: {
    onChange: { action: 'changed' },
    width: { control: 'text' },
    placeholder: { control: 'text' },
    value: { control: 'text' },
    disabled: { control: 'boolean' },
  },
  decorators: [
    (Story) => (
      <div style={{ padding: '40px', minHeight: '40vh' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof CategorySelector>;

export const Default: Story = {
  args: {
    options,
    placeholder: '카테고리 선택',
  },
};

export const WithSelected: Story = {
  args: {
    options,
    placeholder: '카테고리 선택',
    value: 'dog',
  },
};

export const Disabled: Story = {
  args: {
    options,
    placeholder: '카테고리 선택',
    disabled: true,
  },
};
