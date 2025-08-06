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

export interface FilterSectionProps {
  selectedFilter: string | null;
  onFilterChange: (filter: string | null) => void;
  selectedSort: string;
  onSortChange: (sort: string) => void;
  customCSS?: SerializedStyles;
  isAdmin?: boolean;
}

export default function FilterSection({
  selectedFilter,
  onFilterChange,
  selectedSort,
  onSortChange,
  customCSS,
  isAdmin,
}: FilterSectionProps) {
  const theme = useAppTheme();

  const filterOptions = [
    { value: 'pending', label: '미처리' },
    { value: 'completed', label: '완료' },
    ...(!isAdmin ? [{ value: 'mine', label: '내가 쓴 글' }] : []),
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
          onChange={(e) => onSortChange(e.target.value)}
          placeholder='정렬 기준'
          width='120px'
          height='36px'
        />
      </div>
    </div>
  );
}
