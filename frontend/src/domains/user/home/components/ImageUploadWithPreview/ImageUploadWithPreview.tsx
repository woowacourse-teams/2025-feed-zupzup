import {
  container,
  controlRow,
  fileInfo,
  fileInput,
  previewImage,
  previewWrap,
  uploadLabel,
} from '@/domains/user/home/components/ImageUploadWithPreview/ImageUploadWithPreview.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useMemo } from 'react';

interface ImageUploadWithPreview {
  file: File | null;
  imgUrl: string | null;
  onChangeFile: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export default function ImageUploadWithPreview({
  file,
  imgUrl,
  onChangeFile,
}: ImageUploadWithPreview) {
  const theme = useAppTheme();

  const fileName = useMemo(() => file?.name ?? '선택된 파일 없음', [file]);

  return (
    <div css={container}>
      <div css={controlRow}>
        <label css={uploadLabel(theme)}>
          <input
            type='file'
            accept={'image/*'}
            onChange={onChangeFile}
            aria-label='이미지 업로드'
            css={fileInput}
          />
          이미지 업로드
        </label>

        <p css={fileInfo(theme)} aria-live='polite'>
          {fileName}
        </p>
      </div>

      {imgUrl && (
        <div id='image-preview' css={previewWrap}>
          <img src={imgUrl} alt='업로드한 이미지 미리보기' css={previewImage} />
        </div>
      )}
    </div>
  );
}
