import { useAppTheme } from '@/hooks/useAppTheme';
import Selector, {
  SelectorOption,
} from '@/components/@commons/Selector/Selector';
import { categorySelector } from './CategorySelector.styles';

export interface CategorySelectorProps extends React.ComponentProps<'select'> {
  width?: string | number;
  options: SelectorOption[];
  placeholder: string;
  value?: string;
  onChange?: (event: React.ChangeEvent<HTMLSelectElement>) => void;
}

export default function CategorySelector({
  width = 'auto',
  options,
  placeholder,
  value,
  onChange,
  id,
  name,
  ...props
}: CategorySelectorProps) {
  const theme = useAppTheme();

  return (
    <Selector
      css={categorySelector(theme, width)}
      options={options}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      id={id}
      name={name}
      {...props}
    />
  );
}
