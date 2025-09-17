import { profileBox, adminName, adminId } from './ProfileBox.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface ProfileBoxProps {
  name: string;
  id: string;
  isLoading: boolean;
}

export default function ProfileBox({ name, id, isLoading }: ProfileBoxProps) {
  const theme = useAppTheme();
  return (
    <div css={profileBox(theme)}>
      <p css={[adminName(theme), { opacity: isLoading ? 0 : 1 }]}>{name}</p>
      <p css={[adminId(theme), { opacity: isLoading ? 0 : 1 }]}>{id}</p>
    </div>
  );
}
