import { useCallback } from 'react';
import { ApiError } from '@/apis/apiClient';
import { useModalActions } from '@/stores/Modal/useModal';
import AlertModal from '@/components/AlertModal/AlertModal';

export function useErrorModal() {
  const { openModal, closeModal } = useModalActions();
  const showErrorModal = useCallback((error: unknown, title?: string) => {
    let message = '';

    if (error instanceof ApiError) {
      message = error.message;
    } else if (typeof error === 'string') {
      message = error;
    } else {
      message = '알 수 없는 에러가 발생했습니다.';
    }

    openModal(
      <AlertModal
        onClose={closeModal}
        title={title ?? '에러 발생'}
        message={message}
      />
    );
  }, []);

  return { showErrorModal };
}
