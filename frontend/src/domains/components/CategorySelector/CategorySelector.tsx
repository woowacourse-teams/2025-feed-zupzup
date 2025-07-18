import { useState, useEffect } from 'react';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  dropdownContainer,
  dropdownButton,
  dropdownList,
  dropdownItem,
  dropdownText,
} from './CategorySelector.styles';
import ArrowBottom from '@/components/icons/ArrowBottom';

export interface SelectorOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface CategorySelectorProps {
  width?: string | number;
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

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as Element;
      if (!target.closest('[data-category-selector]')) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener('click', handleClickOutside);
      return () => document.removeEventListener('click', handleClickOutside);
    }
  }, [isOpen]);

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener('keydown', handleKeyDown);
      return () => document.removeEventListener('keydown', handleKeyDown);
    }
  }, [isOpen]);

  return (
    <div css={dropdownContainer(theme, width)} data-category-selector>
      <input type='hidden' name={name} id={id} value={value || ''} />
      <button
        type='button'
        css={dropdownButton(theme, isOpen)}
        onClick={handleToggle}
        disabled={disabled}
        aria-haspopup='listbox'
        aria-expanded={isOpen}
        aria-label={placeholder}
      >
        <span css={dropdownText(theme, !!selectedOption)}>{displayText}</span>
        <ArrowBottom />
      </button>

      {isOpen && (
        <div css={dropdownList(theme)} role='listbox'>
          {options.map((option) => (
            <div
              key={option.value}
              css={dropdownItem(
                theme,
                option.value === value,
                !!option.disabled
              )}
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
