import { useState } from 'react';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import { useWithdraw } from '../../hooks/useWithdraw';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  withdrawModalContent,
  withdrawWarningText,
  withdrawDeletedItemsList,
  withdrawAgreementLabel,
  withdrawCheckbox,
} from './WithdrawModal.style';

interface WithdrawModalProps {
  onClose: () => void;
}

export default function WithdrawModal({ onClose }: WithdrawModalProps) {
  const [isWithdrawChecked, setIsWithdrawChecked] = useState(false);
  const theme = useAppTheme();
  const { handleWithdraw } = useWithdraw();

  const handleClose = () => {
    setIsWithdrawChecked(false);
    onClose();
  };

  return (
    <ConfirmModal
      title='회원탈퇴'
      onConfirm={handleWithdraw}
      onClose={handleClose}
      confirmText='탈퇴하기'
      disabled={!isWithdrawChecked}
    >
      <div css={withdrawModalContent}>
        <p css={withdrawWarningText(theme)}>
          탈퇴 시 정보가 삭제되며 복구 불가합니다.
        </p>
        <ul css={withdrawDeletedItemsList(theme)}>
          <li>관리자 계정 및 로그인 정보</li>
          <li>서비스 내 모든 정보</li>
        </ul>
        <label css={withdrawAgreementLabel(theme)}>
          <input
            type='checkbox'
            checked={isWithdrawChecked}
            onChange={(e) => setIsWithdrawChecked(e.target.checked)}
            css={withdrawCheckbox}
          />
          <span>위 내용을 모두 확인했고, 탈퇴하는 것에 동의합니다.</span>
        </label>
      </div>
    </ConfirmModal>
  );
}
