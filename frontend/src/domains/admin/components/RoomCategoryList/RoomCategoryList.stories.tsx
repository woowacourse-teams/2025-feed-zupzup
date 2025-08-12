import RoomCategoryList from '@/domains/admin/components/RoomCategoryList/RoomCategoryList';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta: Meta<typeof RoomCategoryList> = {
  title: 'components/RoomCategoryList',
  component: RoomCategoryList,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <div
        style={{
          maxWidth: '600px',
          height: '80vh',
        }}
      >
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof RoomCategoryList>;

export const Default: Story = {
  render: () => {
    return <RoomCategoryList />;
  },
};
