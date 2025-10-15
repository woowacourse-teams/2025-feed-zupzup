import { useAppTheme } from '@/hooks/useAppTheme';
import {
  loadingContainer,
  loadingImageContainer,
  spinner,
  loadingText,
} from './Loading.styles';

export default function Loading() {
  const theme = useAppTheme();

  return (
    <div css={loadingContainer}>
      <div css={loadingImageContainer}>
        <div css={spinner}></div>
        <div css={loadingText(theme)}>로딩 중...</div>
      </div>
    </div>
  );
}
