import { SerializedStyles } from '@emotion/react';
import { useAppTheme } from '@/hooks/useAppTheme';
import Tag from '@/components/Tag/Tag';
import Dropdown from '@/domains/components/Dropdown/Dropdown'; // CategorySelector 사용
import {
  filterSectionContainer,
  filterTagsContainer,
  tagStyle,
  tagButton,
  sortDropdownContainer,
} from './FilterSection.styles';
import Button from '@/components/@commons/Button/Button';
import { FeedbackFilterType, SortType } from '@/types/feedback.types';

export interface FilterSectionProps {
  selectedFilter: FeedbackFilterType | null;
  onFilterChange: (filter: FeedbackFilterType | null) => void;
  selectedSort: SortType;
  onSortChange: (sort: SortType) => void;
  customCSS?: SerializedStyles;
  isAdmin: boolean;
}

const filterOptions = (isAdmin: boolean) => [
  { value: 'WAITING' as FeedbackFilterType, label: '미처리' },
  { value: 'CONFIRMED' as FeedbackFilterType, label: '완료' },
  ...(!isAdmin
    ? [{ value: 'MINE' as FeedbackFilterType, label: '내가 쓴 글' }]
    : []),
];

const sortOptions = [
  { value: 'LATEST' as SortType, label: '최신순' },
  { value: 'OLDEST' as SortType, label: '오래된순' },
  { value: 'LIKES' as SortType, label: '좋아요순' },
];

export default function FilterSection({
  selectedFilter,
  onFilterChange,
  selectedSort,
  onSortChange,
  customCSS,
  isAdmin,
}: FilterSectionProps) {
  const theme = useAppTheme();

  const handleFilterClick = (filterValue: FeedbackFilterType) => {
    if (selectedFilter === filterValue) {
      onFilterChange(null);
    } else {
      onFilterChange(filterValue);
    }
  };

  return (
    <div css={[filterSectionContainer, customCSS]}>
      <div css={filterTagsContainer}>
        {filterOptions(isAdmin ?? false).map((option) => (
          <Tag
            key={option.value}
            customCSS={tagStyle(theme, selectedFilter === option.value)}
          >
            <Button
              type='button'
              customCSS={tagButton(theme)}
              onClick={() => handleFilterClick(option.value)}
            >
              {option.label}
            </Button>
          </Tag>
        ))}
      </div>

      <div css={sortDropdownContainer}>
        <Dropdown
          options={sortOptions}
          value={selectedSort}
          onChange={(e) => onSortChange(e.target.value as SortType)}
          placeholder='정렬 기준'
          width='100px'
          height='32px'
        />
      </div>
    </div>
  );
}
