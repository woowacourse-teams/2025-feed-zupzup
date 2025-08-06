import { SerializedStyles } from '@emotion/react';
import { useAppTheme } from '@/hooks/useAppTheme';
import Tag from '@/components/Tag/Tag';
import CategorySelector from '@/domains/components/CategorySelector/CategorySelector'; // CategorySelector 사용
import {
  filterSectionContainer,
  filterTagsContainer,
  tagStyle,
  tagButton,
  sortDropdownContainer,
} from './FilterSection.styles';

export interface FilterSectionProps {
  selectedFilter: string | null;
  onFilterChange: (filter: string | null) => void;
  selectedSort: string;
  onSortChange: (sort: string) => void;
  customCSS?: SerializedStyles;
}

export default function FilterSection({
  selectedFilter,
  onFilterChange,
  selectedSort,
  onSortChange,
  customCSS,
}: FilterSectionProps) {
  const theme = useAppTheme();

  const filterOptions = [
    { value: 'pending', label: '미처리' },
    { value: 'completed', label: '완료' },
    { value: 'mine', label: '내가 쓴 글' },
  ];

  const sortOptions = [
    { value: 'latest', label: '최신순' },
    { value: 'oldest', label: '오래된순' },
    { value: 'likes', label: '좋아요순' },
  ];

  const handleFilterClick = (filterValue: string) => {
    if (selectedFilter === filterValue) {
      onFilterChange(null);
    } else {
      onFilterChange(filterValue);
    }
  };

  return (
    <div css={[filterSectionContainer, customCSS]}>
      <div css={filterTagsContainer}>
        {filterOptions.map((option) => (
          <Tag
            key={option.value}
            customCSS={tagStyle(theme, selectedFilter === option.value)}
          >
            <button
              type='button'
              css={tagButton}
              onClick={() => handleFilterClick(option.value)}
            >
              {option.label}
            </button>
          </Tag>
        ))}
      </div>

      <div css={sortDropdownContainer}>
        <CategorySelector
          options={sortOptions}
          value={selectedSort}
          onChange={(e) => onSortChange(e.target.value)}
          placeholder='정렬 기준'
          width='120px'
          height='44px'
        />
      </div>
    </div>
  );
}
