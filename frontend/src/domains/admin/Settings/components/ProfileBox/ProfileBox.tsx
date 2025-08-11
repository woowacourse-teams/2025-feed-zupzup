import { profileBox, adminName, adminId } from './ProfileBox.style';
import { useAppTheme } from '@/hooks/useAppTheme';

export default function ProfileBox({ name, id }: { name: string; id: string }) {
  const theme = useAppTheme();
  return (
    <div css={profileBox(theme)}>
      <p css={adminName(theme)}>{name}</p>
      <p css={adminId(theme)}>{id}</p>
    </div>
  );
}
