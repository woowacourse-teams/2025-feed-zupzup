import BasicButton from '@/components/BasicButton/BasicButton';
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
  onCancelFile: () => void;
}

export default function ImageUploadWithPreview({
  file,
  imgUrl,
  onChangeFile,
  onCancelFile,
}: ImageUploadWithPreview) {
  const theme = useAppTheme();

  const fileName = useMemo(() => file?.name ?? '선택된 파일 없음', [file]);

  return (
    <div css={container}>
      <div css={controlRow}>
        {!imgUrl ? (
          <>
            <label
              css={uploadLabel(theme)}
              aria-label={'이미지 업로드, 선택된 파일 없음'}
            >
              <input
                type='file'
                accept={'image/*'}
                onChange={onChangeFile}
                css={fileInput}
              />
              <span aria-hidden='true'>이미지 업로드</span>
            </label>
          </>
        ) : (
          <BasicButton
            onClick={onCancelFile}
            height='32px'
            width='110px'
            padding='0px'
            aria-label={`업로드한 ${fileName} 제거`}
          >
            이미지 제거
          </BasicButton>
        )}

        <p css={fileInfo(theme)} aria-hidden={true}>
          {fileName}
        </p>

        {imgUrl && (
          <p
            className='srOnly'
            aria-live='assertive'
            role='status'
            aria-atomic='true'
          >
            <span className='srOnly'>선택된 파일: {fileName}</span>
          </p>
        )}
      </div>

      {imgUrl && (
        <div id='image-preview' css={previewWrap}>
          <img src={imgUrl} alt='업로드한 이미지 미리보기' css={previewImage} />
        </div>
      )}
    </div>
  );
}
