import RoomCategoryItem from '@/domains/admin/components/RoomCategoryItem/RoomCategoryItem';
import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';

const meta: Meta<typeof RoomCategoryItem> = {
  title: 'components/RoomCategoryItem',
  component: RoomCategoryItem,
  tags: ['autodocs'],
  argTypes: {
    icon: { control: 'text' },
    category: { control: 'text' },
    isSelected: { control: 'boolean' },
    onClick: { action: 'clicked' },
  },
  decorators: [
    (Story) => (
      <div
        style={{
          maxWidth: '600px',
          height: '80vh',
          backgroundColor: '#f0f0f0',
          padding: '20px',
        }}
      >
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof RoomCategoryItem>;

export const Default: Story = {
  render: () => {
    const [isSelected, setIsSelected] = useState(false);

    return (
      <RoomCategoryItem
        icon='📁'
        category='회의실'
        isSelected={isSelected}
        onClick={() => {
          setIsSelected(!isSelected);
        }}
      />
    );
  },
};

export const Selected: Story = {
  render: () => {
    return (
      <RoomCategoryItem
        icon='📁'
        category='회의실'
        isSelected={true}
        onClick={() => {}}
      />
    );
  },
};
