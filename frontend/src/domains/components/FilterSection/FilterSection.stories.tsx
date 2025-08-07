import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';
import FilterSection from './FilterSection';
import { FeedbackFilterType, SortType } from '@/types/feedback.types';

const meta: Meta<typeof FilterSection> = {
  title: 'components/FilterSection',
  component: FilterSection,
  tags: ['autodocs'],
  argTypes: {
    onFilterChange: { action: 'filter changed' },
    onSortChange: { action: 'sort changed' },
  },
  decorators: [
    (Story) => (
      <div style={{ padding: '20px', maxWidth: '800px' }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof FilterSection>;

export const Default: Story = {
  render: () => {
    const [selectedFilter, setSelectedFilter] =
      useState<FeedbackFilterType | null>(null);
    const [selectedSort, setSelectedSort] = useState<SortType>('LATEST');

    return (
      <FilterSection
        selectedFilter={selectedFilter}
        onFilterChange={setSelectedFilter}
        selectedSort={selectedSort}
        onSortChange={setSelectedSort}
      />
    );
  },
};

export const WithPendingFilter: Story = {
  args: {
    selectedFilter: '미처리',
    selectedSort: 'LATEST',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};

export const WithCompletedFilter: Story = {
  args: {
    selectedFilter: '완료',
    selectedSort: 'LATEST',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};

export const WithMineFilter: Story = {
  args: {
    selectedFilter: '나의글',
    selectedSort: 'LATEST',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};

export const WithLikesSort: Story = {
  args: {
    selectedFilter: null,
    selectedSort: 'LIKES',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};

export const WithOldestSort: Story = {
  args: {
    selectedFilter: null,
    selectedSort: 'OLDEST',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};
