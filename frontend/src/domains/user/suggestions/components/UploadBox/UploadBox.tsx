import Photo from '@/components/icons/photo';
import {
  previewImage,
  uploadBox,
  uploadButton,
  uploadText,
} from '@/domains/user/suggestions/components/UploadBox/UploadBox.style';
import { useAppTheme } from '@/hooks/useAppTheme';
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
