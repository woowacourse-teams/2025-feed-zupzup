import Photo from '@/components/icons/photo';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Theme } from '@/theme';
import { css } from '@emotion/react';
import { useRef, useState } from 'react';

export default function UploadBox() {
  const theme = useAppTheme();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [preview, setPreview] = useState<string | null>(null);

  const handleClick = () => {
    fileInputRef.current?.click();
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const url = URL.createObjectURL(file);
      setPreview(url);
    }
  };

  return (
    <div css={uploadBox(theme)} onClick={handleClick}>
      {preview ? (
        <img src={preview} alt='미리보기' css={previewImage} />
      ) : (
        <>
          <Photo />
          <p css={uploadText(theme)}>사진을 업로드 하세요</p>
        </>
      )}
      <button css={uploadButton(theme)}>사진 선택</button>
      <input
        type='file'
        accept='image/*'
        ref={fileInputRef}
        style={{ display: 'none' }}
        onChange={handleChange}
      />
    </div>
  );
}

const uploadBox = (theme: Theme) => css`
  width: 100%;
  border: 4px dotted ${theme.colors.gray[200]};
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  justify-content: center;
  align-items: center;
  padding: 30px;
  cursor: pointer;

  :hover {
    background-color: whitesmoke;
  }
`;

const uploadText = (theme: Theme) => css`
  color: ${theme.colors.gray[500]};
  ${theme.typography.inter.bodyRegular}
`;

const uploadButton = (theme: Theme) => css`
  color: white;
  background-color: ${theme.colors.yellow[200]};

  border: 1px solid ${theme.colors.yellow[200]};
  border-radius: 9999px;
  ${theme.typography.inter.small}
  padding: 8px 16px;
`;

const previewImage = css`
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
`;
