import {
  authDescription,
  authLayout,
  authTitle,
  titleContainer,
} from '@/domains/admin/components/AuthLayout/AuthLayout.style';
import { useAppTheme } from '@/hooks/useAppTheme';

interface AuthLayoutProps {
  children: React.ReactNode;
  title: string;
  caption: string;
}

export default function AuthLayout({
  children,
  title,
  caption,
}: AuthLayoutProps) {
  const theme = useAppTheme();
  return (
    <div css={authLayout(theme)}>
      <div css={titleContainer(theme)}>
        <p css={authTitle(theme)}>{title}</p>
        <p css={authDescription(theme)}>{caption}</p>
      </div>
      {children}
    </div>
  );
}
