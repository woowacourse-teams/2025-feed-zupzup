import { SerializedStyles } from '@emotion/react';
import ArrowBottom from '@/components/icons/ArrowBottom';

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
  id,
  name,
  ...props
}: SelectorProps) {
  const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    onChange?.(event);
  };

  return (
    <div style={{ position: 'relative', display: 'inline-block' }}>
      <select
        css={css}
        value={value}
        onChange={handleChange}
        disabled={disabled}
        id={id}
        name={name}
        {...props}
      >
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
      <div
        style={{
          position: 'absolute',
          right: '12px',
          top: '50%',
          transform: 'translateY(-50%)',
          pointerEvents: 'none',
          zIndex: 1,
        }}
      >
        <ArrowBottom />
      </div>
    </div>
  );
}
