import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { useState } from 'react';
import FilterSection from './FilterSection';

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
    const [selectedFilter, setSelectedFilter] = useState<string | null>(null);
    const [selectedSort, setSelectedSort] = useState('latest');

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
    selectedFilter: 'pending',
    selectedSort: 'latest',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};

export const WithCompletedFilter: Story = {
  args: {
    selectedFilter: 'completed',
    selectedSort: 'latest',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};

export const WithMineFilter: Story = {
  args: {
    selectedFilter: 'mine',
    selectedSort: 'latest',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};

export const WithLikesSort: Story = {
  args: {
    selectedFilter: null,
    selectedSort: 'likes',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};

export const WithOldestSort: Story = {
  args: {
    selectedFilter: null,
    selectedSort: 'oldest',
    onFilterChange: () => {},
    onSortChange: () => {},
  },
};
