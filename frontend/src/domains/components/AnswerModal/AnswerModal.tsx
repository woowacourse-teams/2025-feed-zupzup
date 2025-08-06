import TextArea from '@/components/@commons/TextArea/TextArea';
import BasicButton from '@/components/BasicButton/BasicButton';
import { headerSubtitle, headerTitle } from '@/components/Header/Header.style';
import Modal from '@/components/Modal/Modal';
import TextareaCounter from '@/components/TextareaCounter/TextareaCounter';
import {
  buttonContainer,
  contentContainer,
  headerContainer,
  container,
  contentTextarea,
  textareaContainer,
} from '@/domains/components/AnswerModal/AnswerModal.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState } from 'react';

interface AnswerModalProps {
  isOpen: boolean;
  handleCloseModal: () => void;
  handleSubmit: () => void;
}

export default function AnswerModal({
  isOpen,
  handleCloseModal,
  handleSubmit,
}: AnswerModalProps) {
  const [answer, setAnswer] = useState('');
  const theme = useAppTheme();

  const handleAnswerChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setAnswer(e.target.value);
  };

  return (
    <Modal isOpen={isOpen} onClose={handleCloseModal} customCSS={container}>
      <div css={headerContainer}>
        <p css={headerTitle(theme)}>관리자 답변</p>
        <p css={headerSubtitle(theme)}>이 피드백에 답변을 남겨주세요</p>
      </div>
      <div css={contentContainer(theme)}>
        <div css={textareaContainer}>
          <TextArea
            minLength={1}
            maxLength={200}
            value={answer}
            onChange={handleAnswerChange}
            placeholder='사용자에게 전달할 메시지를 작성해주세요.(선택사항)'
            customCSS={contentTextarea(theme)}
          />
          <TextareaCounter textLength={answer.length} />
        </div>
        <p>입력하지 않으면 "확인했습니다."로 자동 전송됩니다.</p>
      </div>
      <div css={buttonContainer(theme)}>
        <BasicButton
          variant={'secondary'}
          width={'47%'}
          onClick={handleCloseModal}
        >
          취소
        </BasicButton>
        <BasicButton width={'47%'} onClick={handleSubmit}>
          답변 전송
        </BasicButton>
      </div>
    </Modal>
  );
}
