import Button from '@/components/@commons/Button/Button';
import PhotoIcon from '@/components/icons/PhotoIcon';
import {
  previewImage,
  uploadBox,
  uploadButton,
  uploadText,
} from '@/domains/user/suggestions/components/UploadBox/UploadBox.style';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useRef } from 'react';

interface UploadBoxProps {
  imgSrc: string | null;
  handleImageUpload: (url: string) => void;
}

export default function UploadBox({
  imgSrc,
  handleImageUpload,
}: UploadBoxProps) {
  const theme = useAppTheme();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleClick = () => {
    fileInputRef.current?.click();
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const url = URL.createObjectURL(file);
      handleImageUpload(url);
    }
  };

  return (
    <div css={uploadBox(theme)} onClick={handleClick}>
      {imgSrc ? (
        <img src={imgSrc} alt='미리보기' css={previewImage} />
      ) : (
        <>
          <PhotoIcon />
          <p css={uploadText(theme)}>사진을 업로드 하세요</p>
        </>
      )}
      <Button css={uploadButton(theme)} type='button'>
        사진 선택
      </Button>
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
