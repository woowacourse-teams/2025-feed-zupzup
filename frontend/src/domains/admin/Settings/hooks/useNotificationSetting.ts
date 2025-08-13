import { useState, useEffect, useCallback } from 'react';
import { useNotifications } from '@/domains/admin/Settings/hooks/useNotifications';
import {
  registerFCMToken,
  updateNotificationSettings,
} from '@/apis/notifications.api';

export const useNotificationSetting = () => {
  const {
    token,
    permission,
    isSupported,
    loading: fcmLoading,
    generateToken,
    requestPermission,
  } = useNotifications();

  const [isToggleEnabled, setIsToggleEnabled] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const organizationId = 1;

  useEffect(() => {
    // TODO: 나중에 getNotificationSettings API가 구현되면 활성화
    // const loadNotificationSettings = async () => {
    //   try {
    //     const response = await getNotificationSettings(organizationId);
    //     setIsToggleEnabled(response.data.notificationEnabled);
    //   } catch (error) {
    //     console.error('알림 설정 로드 실패:', error);
    //     // 로드 실패 시 기본값 유지 (false)
    //   }
    // };
    // loadNotificationSettings();

    const savedState = localStorage.getItem(
      `notification_enabled_${organizationId}`
    );
    if (savedState !== null) {
      setIsToggleEnabled(JSON.parse(savedState));
    }
  }, [organizationId]);

  const updateNotificationSetting = useCallback(
    async (enabled: boolean) => {
      if (!isSupported) {
        setError('이 브라우저는 푸시 알림을 지원하지 않습니다.');
        return;
      }

      setIsLoading(true);
      setError(null);

      try {
        if (enabled) {
          // 알림 활성화 프로세스

          // 1. 권한 확인/요청
          if (permission !== 'granted') {
            await requestPermission();
          }

          // 2. FCM 토큰 생성/확인
          let fcmToken = token;
          if (!fcmToken) {
            fcmToken = await generateToken();
          }

          // 3. 백엔드에 토큰 등록
          await registerFCMToken(fcmToken);
          console.log('[FCM] 토큰 등록 완료');

          // 4. 알림 설정 활성화
          await updateNotificationSettings(organizationId, true);

          setIsToggleEnabled(true);

          localStorage.setItem(
            `notification_enabled_${organizationId}`,
            'true'
          );
          console.log('[FCM] 알림이 활성화되었습니다.');
        } else {
          // 알림 비활성화
          await updateNotificationSettings(organizationId, false);
          setIsToggleEnabled(false);

          localStorage.setItem(
            `notification_enabled_${organizationId}`,
            'false'
          );
          console.log('[FCM] 알림이 비활성화되었습니다.');
        }
      } catch (error) {
        console.error('[FCM] 알림 설정 변경 실패:', error);

        // 사용자 친화적인 에러 메시지
        if (error instanceof Error) {
          if (error.message.includes('권한')) {
            setError('알림 권한을 허용해주세요.');
          } else if (error.message.includes('토큰')) {
            setError('알림 설정 중 오류가 발생했습니다. 다시 시도해주세요.');
          } else {
            setError('알림 설정 변경에 실패했습니다.');
          }
        } else {
          setError('알 수 없는 오류가 발생했습니다.');
        }

        // 에러 발생 시 토글 상태 되돌리기 (localStorage도 복원)
        const previousState = !enabled;
        setIsToggleEnabled(previousState);
        localStorage.setItem(
          `notification_enabled_${organizationId}`,
          String(previousState)
        );
      } finally {
        setIsLoading(false);
      }
    },
    [
      isSupported,
      permission,
      token,
      organizationId,
      requestPermission,
      generateToken,
    ]
  );

  const sendTestNotification = useCallback(async () => {
    if (!isToggleEnabled) {
      setError('먼저 알림을 활성화해주세요.');
      return;
    }

    try {
      const { sendTestNotification: sendTest } = await import(
        '@/apis/notifications.api'
      );
      const response = await sendTest(organizationId);
      console.log('[FCM] 테스트 알림 발송 완료:', response);
      return response;
    } catch (error) {
      console.error('[FCM] 테스트 알림 발송 실패:', error);
      setError('테스트 알림 발송에 실패했습니다.');
      throw error;
    }
  }, [isToggleEnabled, organizationId]);

  return {
    isToggleEnabled,
    updateNotificationSetting,
    isLoading: isLoading || fcmLoading,

    fcmStatus: {
      isSupported,
      permission,
      hasToken: !!token,
      error,
    },

    sendTestNotification:
      process.env.NODE_ENV === 'development' ? sendTestNotification : undefined,

    clearError: () => setError(null),
  };
};
