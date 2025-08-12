import { CategoryListType } from '@/constants/categoryList';
import RoomCategoryTagList from '@/domains/admin/components/RoomCategoryTagList/RoomCategoryTagList';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof RoomCategoryTagList> = {
  title: 'components/RoomCategoryTagList',
  component: RoomCategoryTagList,
  tags: ['autodocs'],
  argTypes: {
    selectedCategory: {
      control: 'multi-select',
    },
    handleCategoryTagClick: {
      action: 'category tag clicked',
    },
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

type Story = StoryObj<typeof RoomCategoryTagList>;

export const Default: Story = {
  render: () => {
    const selectedCategory: { icon: string; category: CategoryListType }[] = [
      { icon: '📚', category: '신고' },
      { icon: '🎨', category: '칭찬' },
      { icon: '🎮', category: '피드백' },
    ];

    return (
      <RoomCategoryTagList
        selectedCategory={selectedCategory}
        handleCategoryTagClick={() => {}}
      />
    );
  },
};
