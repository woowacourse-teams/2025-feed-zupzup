import { renderHook, act, waitFor } from '@testing-library/react';
import { useTimeDelayModal } from './useTimeDelayModal';

jest.useFakeTimers();

describe('useTimeDelayModal', () => {
  const mockOnClose = jest.fn();

  const defaultProps = {
    isOpen: false,
    loadingDuration: 2000,
    autoCloseDuration: 3000,
    onClose: mockOnClose,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterAll(() => {
    jest.useRealTimers();
  });

  describe('초기 상태', () => {
    it('모달이 닫힌 상태에서 초기값이 올바르게 설정되어야 한다', () => {
      const { result } = renderHook(() => useTimeDelayModal(defaultProps));

      expect(result.current.isLoading).toBe(false);
      expect(result.current.isComplete).toBe(false);
      expect(typeof result.current.handleModalClose).toBe('function');
    });

    it('모달이 열린 상태에서 초기값이 올바르게 설정되어야 한다', () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({ ...defaultProps, isOpen: true })
      );

      expect(result.current.isLoading).toBe(true);
      expect(result.current.isComplete).toBe(false);
    });
  });

  describe('로딩 단계', () => {
    it('모달이 열리면 로딩 상태가 시작되어야 한다', () => {
      const { result, rerender } = renderHook(
        ({ isOpen }) => useTimeDelayModal({ ...defaultProps, isOpen }),
        { initialProps: { isOpen: false } }
      );

      expect(result.current.isLoading).toBe(false);

      act(() => {
        rerender({ isOpen: true });
      });

      expect(result.current.isLoading).toBe(true);
      expect(result.current.isComplete).toBe(false);
    });

    it('로딩 시간이 완료되면 로딩이 끝나고 완료 상태가 되어야 한다', async () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1000,
        })
      );

      expect(result.current.isLoading).toBe(true);
      expect(result.current.isComplete).toBe(false);

      // 로딩 시간 경과
      act(() => {
        jest.advanceTimersByTime(1000);
      });

      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
        expect(result.current.isComplete).toBe(true);
      });
    });

    it('다른 로딩 시간으로도 정상 동작해야 한다', async () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 5000,
        })
      );

      expect(result.current.isLoading).toBe(true);

      // 중간 시점에서는 여전히 로딩 중
      act(() => {
        jest.advanceTimersByTime(3000);
      });
      expect(result.current.isLoading).toBe(true);
      expect(result.current.isComplete).toBe(false);

      // 로딩 완료
      act(() => {
        jest.advanceTimersByTime(2000);
      });

      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
        expect(result.current.isComplete).toBe(true);
      });
    });

    it('모달이 닫히면 로딩 타이머가 정리되어야 한다', () => {
      const { result, rerender } = renderHook(
        ({ isOpen }) =>
          useTimeDelayModal({ ...defaultProps, isOpen, loadingDuration: 2000 }),
        { initialProps: { isOpen: true } }
      );

      expect(result.current.isLoading).toBe(true);

      act(() => {
        rerender({ isOpen: false });
      });

      expect(result.current.isLoading).toBe(true);

      act(() => {
        jest.advanceTimersByTime(2000);
      });

      expect(result.current.isLoading).toBe(true);
      expect(result.current.isComplete).toBe(false);
    });
  });

  describe('자동 닫기 단계', () => {
    it('완료 상태가 되면 자동 닫기 타이머가 시작되어야 한다', async () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1000,
          autoCloseDuration: 2000,
        })
      );

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      await waitFor(() => {
        expect(result.current.isComplete).toBe(true);
      });

      expect(mockOnClose).not.toHaveBeenCalled();

      act(() => {
        jest.advanceTimersByTime(2000);
      });

      await waitFor(() => {
        expect(mockOnClose).toHaveBeenCalledTimes(1);
      });
    });

    it('다른 자동 닫기 시간으로도 정상 동작해야 한다', async () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 500,
          autoCloseDuration: 5000,
        })
      );

      act(() => {
        jest.advanceTimersByTime(500);
      });

      await waitFor(() => {
        expect(result.current.isComplete).toBe(true);
      });

      act(() => {
        jest.advanceTimersByTime(3000);
      });
      expect(mockOnClose).not.toHaveBeenCalled();

      act(() => {
        jest.advanceTimersByTime(2000);
      });

      await waitFor(() => {
        expect(mockOnClose).toHaveBeenCalledTimes(1);
      });
    });

    it('완료 상태가 아니면 자동 닫기가 시작되지 않아야 한다', () => {
      renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 3000,
          autoCloseDuration: 1000,
        })
      );

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      expect(mockOnClose).not.toHaveBeenCalled();
    });
  });

  describe('수동 닫기 (handleModalClose)', () => {
    it('로딩 중이 아닐 때 handleModalClose를 호출하면 onClose가 호출되어야 한다', () => {
      const { result } = renderHook(() => useTimeDelayModal(defaultProps));

      act(() => {
        result.current.handleModalClose();
      });

      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    it('로딩 중일 때 handleModalClose를 호출해도 onClose가 호출되지 않아야 한다', () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({ ...defaultProps, isOpen: true })
      );

      expect(result.current.isLoading).toBe(true);

      act(() => {
        result.current.handleModalClose();
      });

      expect(mockOnClose).not.toHaveBeenCalled();
    });

    it('로딩이 완료된 후에는 handleModalClose가 다시 동작해야 한다', async () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1000,
        })
      );

      expect(result.current.isLoading).toBe(true);
      act(() => {
        result.current.handleModalClose();
      });
      expect(mockOnClose).not.toHaveBeenCalled();

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
      });

      act(() => {
        result.current.handleModalClose();
      });
      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });
  });

  describe('상태 리셋', () => {
    it('모달이 다시 열릴 때 상태가 초기화되어야 한다', async () => {
      const { result, rerender } = renderHook(
        ({ isOpen }) =>
          useTimeDelayModal({ ...defaultProps, isOpen, loadingDuration: 1000 }),
        { initialProps: { isOpen: true } }
      );

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
        expect(result.current.isComplete).toBe(true);
      });

      act(() => {
        rerender({ isOpen: false });
      });

      act(() => {
        rerender({ isOpen: true });
      });

      expect(result.current.isLoading).toBe(true);
      expect(result.current.isComplete).toBe(false);
    });
  });

  describe('의존성 변경', () => {
    it('loadingDuration이 변경되면 새로운 시간으로 동작해야 한다', async () => {
      const { result, rerender } = renderHook(
        ({ loadingDuration }) =>
          useTimeDelayModal({ ...defaultProps, isOpen: true, loadingDuration }),
        { initialProps: { loadingDuration: 2000 } }
      );

      expect(result.current.isLoading).toBe(true);

      act(() => {
        rerender({ loadingDuration: 1000 });
      });

      expect(result.current.isLoading).toBe(true);

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
        expect(result.current.isComplete).toBe(true);
      });
    });

    it('autoCloseDuration이 변경되면 새로운 시간으로 동작해야 한다', async () => {
      const { result, rerender } = renderHook(
        ({ autoCloseDuration }) =>
          useTimeDelayModal({
            ...defaultProps,
            isOpen: true,
            loadingDuration: 500,
            autoCloseDuration,
          }),
        { initialProps: { autoCloseDuration: 3000 } }
      );

      act(() => {
        jest.advanceTimersByTime(500);
      });

      await waitFor(() => {
        expect(result.current.isComplete).toBe(true);
      });

      act(() => {
        rerender({ autoCloseDuration: 1000 });
      });

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      await waitFor(() => {
        expect(mockOnClose).toHaveBeenCalledTimes(1);
      });
    });

    it('onClose 콜백이 변경되면 새로운 콜백이 호출되어야 한다', async () => {
      const mockOnClose2 = jest.fn();

      const { rerender } = renderHook(
        ({ onClose }) =>
          useTimeDelayModal({
            ...defaultProps,
            isOpen: true,
            loadingDuration: 500,
            autoCloseDuration: 1000,
            onClose,
          }),
        { initialProps: { onClose: mockOnClose } }
      );

      act(() => {
        rerender({ onClose: mockOnClose2 });
      });

      act(() => {
        jest.advanceTimersByTime(500);
      });

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      await waitFor(() => {
        expect(mockOnClose).not.toHaveBeenCalled();
        expect(mockOnClose2).toHaveBeenCalledTimes(1);
      });
    });
  });

  describe('타이머 정리', () => {
    it('컴포넌트가 언마운트되면 모든 타이머가 정리되어야 한다', () => {
      const { unmount } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 2000,
          autoCloseDuration: 3000,
        })
      );

      unmount();

      act(() => {
        jest.advanceTimersByTime(5000);
      });

      expect(mockOnClose).not.toHaveBeenCalled();
    });

    it('완료 상태에서 컴포넌트가 언마운트되면 자동 닫기 타이머가 정리되어야 한다', async () => {
      const { result, unmount } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1000,
          autoCloseDuration: 2000,
        })
      );

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      await waitFor(() => {
        expect(result.current.isComplete).toBe(true);
      });

      unmount();

      act(() => {
        jest.advanceTimersByTime(2000);
      });

      expect(mockOnClose).not.toHaveBeenCalled();
    });
  });

  describe('전체 플로우', () => {
    it('전체 플로우가 정상적으로 동작해야 한다', async () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 2000,
          autoCloseDuration: 1000,
        })
      );

      expect(result.current.isLoading).toBe(true);
      expect(result.current.isComplete).toBe(false);

      act(() => {
        jest.advanceTimersByTime(2000);
      });

      await waitFor(() => {
        expect(result.current.isLoading).toBe(false);
        expect(result.current.isComplete).toBe(true);
      });

      expect(mockOnClose).not.toHaveBeenCalled();

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      await waitFor(() => {
        expect(mockOnClose).toHaveBeenCalledTimes(1);
      });
    });
  });
});
