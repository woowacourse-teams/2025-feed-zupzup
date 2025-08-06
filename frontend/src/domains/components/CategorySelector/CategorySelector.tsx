import { useState, useEffect } from 'react';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  dropdownContainer,
  dropdownButton,
  dropdownList,
  dropdownItem,
  dropdownText,
} from './CategorySelector.styles';
import ArrowBottomIcon from '@/components/icons/ArrowBottomIcon';
import Button from '@/components/@commons/Button/Button';

interface SelectorOption {
  value: string;
  label: string;
  disabled?: boolean;
}

interface CategorySelectorProps {
  width?: string;
  height?: string;
  options: SelectorOption[];
  placeholder: string;
  value?: string;
  onChange?: (event: React.ChangeEvent<HTMLSelectElement>) => void;
  id?: string;
  name?: string;
  disabled?: boolean;
}

export default function CategorySelector({
  width = 'auto',
  height = 'auto',
  options,
  placeholder,
  value,
  onChange,
  id,
  name,
  disabled = false,
}: CategorySelectorProps) {
  const theme = useAppTheme();
  const [isOpen, setIsOpen] = useState(false);

  const selectedOption = options.find((option) => option.value === value);
  const displayText = selectedOption ? selectedOption.label : placeholder;

  const handleToggle = () => {
    if (!disabled) {
      setIsOpen(!isOpen);
    }
  };

  const handleSelect = (optionValue: string) => {
    const fakeEvent = {
      target: {
        value: optionValue,
        name: name || '',
        id: id || '',
      },
    } as React.ChangeEvent<HTMLSelectElement>;

    onChange?.(fakeEvent);
    setIsOpen(false);
  };

  const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as Element;
    if (!target.closest('[data-category-selector]')) {
      setIsOpen(false);
    }
  };

  const handleKeyDown = (event: KeyboardEvent) => {
    if (event.key === 'Escape') {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      document.addEventListener('click', handleClickOutside);
      document.addEventListener('keydown', handleKeyDown);

      return () => {
        document.removeEventListener('click', handleClickOutside);
        document.removeEventListener('keydown', handleKeyDown);
      };
    }
    return;
  }, [isOpen]);

  return (
    <div css={dropdownContainer(width, height)} data-category-selector>
      <input type='hidden' name={name} id={id} value={value || ''} />
      <Button
        type='button'
        css={dropdownButton(theme, isOpen, height)}
        onClick={handleToggle}
        disabled={disabled}
        aria-haspopup='listbox'
        aria-expanded={isOpen}
        aria-label={placeholder}
      >
        <span css={dropdownText(theme, !!selectedOption)}>{displayText}</span>
        <ArrowBottomIcon />
      </Button>

      {isOpen && (
        <div css={dropdownList(theme)} role='listbox'>
          {options.map((option) => (
            <div
              key={option.value}
              css={dropdownItem(theme, !!option.disabled)}
              onClick={() => !option.disabled && handleSelect(option.value)}
              role='option'
              aria-selected={option.value === value}
              aria-disabled={option.disabled}
            >
              {option.label}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
