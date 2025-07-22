import { useAppTheme } from '@/hooks/useAppTheme';
import { header, imageStyle, wrapper } from './FeedbackImage.styles';
import ImageIcon from '@/components/icons/ImageIcon';

interface FeedbackImageProps {
  src: string;
  alt?: string;
}

export default function FeedbackImage({
  src,
  alt = '첨부 이미지',
}: FeedbackImageProps) {
  const theme = useAppTheme();

  return (
    <div css={wrapper}>
      <div css={header(theme)}>
        <ImageIcon />
        <span>첨부 이미지</span>
      </div>
      <img src={src} alt={alt} css={imageStyle} />
    </div>
  );
}
