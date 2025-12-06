import {
  authDescription,
  authLayout,
  authTitle,
  titleContainer,
} from '@/domains/admin/components/AuthLayout/AuthLayout.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useEffect, useRef } from 'react';

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
  const headingRef = useRef<HTMLHeadingElement>(null);

  useEffect(() => {
    window.scrollTo(0, 0);

    if (headingRef.current) {
      headingRef.current.focus();
    }
  }, []);

  return (
    <div css={authLayout(theme)}>
      <div css={titleContainer(theme)}>
        <h1
          ref={headingRef}
          css={authTitle(theme)}
          aria-label={`${title} ${caption}`}
          tabIndex={-1}
        >
          {title}
        </h1>
        <p css={authDescription(theme)} aria-hidden='true'>
          {caption}
        </p>
      </div>
      {children}
    </div>
  );
}
