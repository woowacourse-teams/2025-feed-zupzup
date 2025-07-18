import { SerializedStyles } from '@emotion/react';

export interface SelectorOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface SelectorProps extends React.ComponentProps<'select'> {
  options: SelectorOption[];
  placeholder?: string;
  css?: SerializedStyles;
}

export default function Selector({
  options,
  value,
  onChange,
  placeholder,
  disabled,
  css,
}: SelectorProps) {
  const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    onChange?.(event);
  };

  return (
    <select css={css} value={value} onChange={handleChange} disabled={disabled}>
      {placeholder && (
        <option value='' disabled>
          {placeholder}
        </option>
      )}
      {options.map((option) => (
        <option
          key={option.value}
          value={option.value}
          disabled={option.disabled}
        >
          {option.label}
        </option>
      ))}
    </select>
  );
}
