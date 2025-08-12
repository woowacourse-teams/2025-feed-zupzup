import { profileBox, adminName, adminId } from './ProfileBox.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface ProfileBoxProps {
  name: string;
  id: string;
}

export default function ProfileBox({ name, id }: ProfileBoxProps) {
  const theme = useAppTheme();
  return (
    <div css={profileBox(theme)}>
      <p css={adminName(theme)}>{name}</p>
      <p css={adminId(theme)}>{id}</p>
    </div>
  );
}
