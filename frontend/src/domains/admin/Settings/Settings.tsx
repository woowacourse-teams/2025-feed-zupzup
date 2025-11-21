import { SEO } from '@/components/SEO/SEO';
import { useState } from 'react';
import BasicToggleButton from '@/components/BasicToggleButton/BasicToggleButton';
import BellOutlineIcon from '@/components/icons/BellOutlineIcon';
import OutOutlineIcon from '@/components/icons/OutOutlineIcon';
import SendIcon from '@/components/icons/SendIcon';
import TrashCanIcon from '@/components/icons/TrashCanIcon';
import ConfirmModal from '@/components/ConfirmModal/ConfirmModal';
import ProfileBox from './components/ProfileBox/ProfileBox';
import SettingListBox from './components/SettingListBox/SettingListBox';
import { useLogout } from './hooks/useLogout';
import { useWithdraw } from './hooks/useWithdraw';
import { useNotificationSettingsPage } from './hooks/useNotificationSettingsPage';
import {
  settingsContainer,
  withdrawModalContent,
  withdrawWarningText,
  withdrawDeletedItemsList,
  withdrawAgreementLabel,
  withdrawCheckbox,
  withdrawSettingListBox,
} from './Settings.style';
import useAdminAuth from '@/domains/admin/Settings/hooks/useAdminAuth';
import { usePWAPrompt } from '@/contexts/usePWAPrompt';
import { useAppTheme } from '@/hooks/useAppTheme';

declare global {
  interface Window {
    ChannelIO?: {
      (...args: unknown[]): void;
      showMessenger?: () => void;
    };
  }
}

type ModalState = { type: 'logout' } | { type: 'withdraw' } | { type: null };

export default function Settings() {
  const [modalState, setModalState] = useState<ModalState>({ type: null });
  const [isWithdrawChecked, setIsWithdrawChecked] = useState(false);
  const theme = useAppTheme();
  const {
    isToggleEnabled,
    updateNotificationSetting,
    isLoading,
    fcmStatus,
    needsPWAInstall,
  } = useNotificationSettingsPage();
  const { adminAuth, isLoading: isAdminAuthLoading } = useAdminAuth();
  const { handleLogout } = useLogout();
  const { handleWithdraw } = useWithdraw();
  const { showPrompt } = usePWAPrompt();

  const closeModal = () => {
    setModalState({ type: null });
    setIsWithdrawChecked(false);
  };

  const handleToggleClick = () => {
    updateNotificationSetting(!isToggleEnabled);
  };

  const handleCustomerServiceClick = () => {
    if (window.ChannelIO) {
      window.ChannelIO('showMessenger');
    }
  };

  const handleNotificationSettingClick = () => {
    if (needsPWAInstall) {
      showPrompt();
    }
  };

  const getNotificationMessage = () => {
    if (needsPWAInstall) {
      return '알림을 받기위해 홈 화면에 앱을 추가해주세요. 터치하면 설치 화면이 나옵니다.';
    }
    return '푸시 알림 받기 설정';
  };

  return (
    <>
      <SEO
        title='설정'
        description='알림 설정 및 계정 관리'
        keywords='설정, 알림, 계정, 관리, settings'
      />
      <div css={settingsContainer}>
        <ProfileBox
          isLoading={isAdminAuthLoading}
          name={adminAuth?.data.adminName || ''}
          id={adminAuth?.data.loginId || ''}
        />

        <SettingListBox
          icon={<BellOutlineIcon />}
          title='알림 설정'
          description={getNotificationMessage()}
          {...(needsPWAInstall && { onClick: handleNotificationSettingClick })}
          rightElement={
            needsPWAInstall ? undefined : (
              <BasicToggleButton
                isToggled={isToggleEnabled}
                onClick={handleToggleClick}
                name='notification-toggle'
                disabled={isLoading || !fcmStatus.isSupported}
              />
            )
          }
        />

        <SettingListBox
          icon={<SendIcon />}
          title='고객센터'
          description='문의사항을 채널톡으로 전달하세요'
          onClick={handleCustomerServiceClick}
        />

        <SettingListBox
          icon={<OutOutlineIcon />}
          title='로그아웃'
          onClick={() => setModalState({ type: 'logout' })}
        />

        <div css={withdrawSettingListBox}>
          <SettingListBox
            icon={<TrashCanIcon />}
            title='회원탈퇴'
            variant='danger'
            onClick={() => setModalState({ type: 'withdraw' })}
          />
        </div>

        {modalState.type === 'logout' && (
          <ConfirmModal
            title='로그아웃'
            message='정말 로그아웃하시겠습니까?'
            onConfirm={handleLogout}
            onClose={closeModal}
          />
        )}

        {modalState.type === 'withdraw' && (
          <ConfirmModal
            title='회원탈퇴'
            onConfirm={handleWithdraw}
            onClose={closeModal}
            confirmText='탈퇴하기'
            confirmDisabled={!isWithdrawChecked}
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
        )}
      </div>
    </>
  );
}
