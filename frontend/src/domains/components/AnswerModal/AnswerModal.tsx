import TextArea from '@/components/@commons/TextArea/TextArea';
import BasicButton from '@/components/BasicButton/BasicButton';
import { headerSubtitle } from '@/components/Header/Header.style';

import Modal from '@/components/Modal/Modal';
import TextareaCounter from '@/components/TextareaCounter/TextareaCounter';
import {
  buttonContainer,
  contentContainer,
  headerContainer,
  container,
  contentTextarea,
  textareaContainer,
  headerTitle,
} from '@/domains/components/AnswerModal/AnswerModal.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { useState } from 'react';

interface AnswerModalProps {
  handleCloseModal: () => void;
  handleSubmit: (comment: string) => void;
}

export default function AnswerModal({
  handleCloseModal,
  handleSubmit,
}: AnswerModalProps) {
  const [answer, setAnswer] = useState('');
  const theme = useAppTheme();

  const handleAnswerChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setAnswer(e.target.value);
  };

  return (
    <Modal onClose={handleCloseModal} customCSS={container}>
      <div css={headerContainer}>
        <p css={headerTitle(theme)}>관리자 답변</p>
        <p css={headerSubtitle(theme)}>이 피드백에 답변을 남겨주세요</p>
      </div>
      <div css={contentContainer(theme)}>
        <div css={textareaContainer}>
          <TextArea
            minLength={1}
            maxLength={500}
            value={answer}
            onChange={handleAnswerChange}
            placeholder='사용자에게 전달할 메시지를 작성해주세요.(선택사항)'
            customCSS={contentTextarea(theme)}
          />
          <TextareaCounter
            textLength={answer.length}
            right='16px'
            bottom='-23px'
          />
        </div>
        {answer.length === 0 ? (
          <p>답변 없이 완료 처리됩니다.</p>
        ) : (
          <p>답변 전송하고 완료 처리됩니다.</p>
        )}
      </div>
      <div css={buttonContainer(theme)}>
        <BasicButton
          variant={'secondary'}
          width={'47%'}
          onClick={handleCloseModal}
        >
          취소
        </BasicButton>
        <BasicButton width={'47%'} onClick={() => handleSubmit(answer)}>
          완료
        </BasicButton>
      </div>
    </Modal>
  );
}
