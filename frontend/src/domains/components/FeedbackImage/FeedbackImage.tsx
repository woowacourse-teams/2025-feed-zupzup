import { useAppTheme } from '@/hooks/useAppTheme';
import { header, imageStyle, wrapper } from './FeedbackImage.styles';
import Image from '@/components/icons/Image';

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
        <Image />
        <span>첨부 이미지</span>
      </div>
      <img src={src} alt={alt} css={imageStyle} />
    </div>
  );
}
