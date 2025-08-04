import { renderHook, act, waitFor } from '@testing-library/react';
import { useTimeDelayModal } from './useTimeDelayModal';

describe('useTimeDelayModal', () => {
  const defaultProps = {
    isOpen: false,
    loadingDuration: 2000,
    autoCloseDuration: 3000,
    onClose: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  describe('초기 상태', () => {
    it('모달이 닫힌 상태에서 초기값이 올바르게 설정되어야 한다', () => {
      const { result } = renderHook(() => useTimeDelayModal(defaultProps));

      expect(result.current.isMinimumDelayActive).toBe(false);
      expect(result.current.isComplete).toBe(false);
      expect(typeof result.current.handleModalClose).toBe('function');
    });

    it('모달이 열린 상태에서 초기값이 올바르게 설정되어야 한다', () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({ ...defaultProps, isOpen: true })
      );

      expect(result.current.isMinimumDelayActive).toBe(true);
      expect(result.current.isComplete).toBe(false);
    });
  });

  describe('로딩 단계', () => {
    it('모달이 열리면 로딩 상태가 시작되어야 한다', () => {
      const { result, rerender } = renderHook(
        (props) => useTimeDelayModal(props),
        { initialProps: defaultProps }
      );

      expect(result.current.isMinimumDelayActive).toBe(false);

      act(() => {
        rerender({ ...defaultProps, isOpen: true });
      });

      expect(result.current.isMinimumDelayActive).toBe(true);
      expect(result.current.isComplete).toBe(false);
    });

    it('로딩 시간이 완료되면 로딩이 끝나고 완료 상태가 되어야 한다', () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1000,
        })
      );

      expect(result.current.isMinimumDelayActive).toBe(true);
      expect(result.current.isComplete).toBe(false);

      // 로딩 시간 경과
      act(() => {
        jest.advanceTimersByTime(1000);
      });

      expect(result.current.isMinimumDelayActive).toBe(false);
      expect(result.current.isComplete).toBe(true);
    });

    it('다른 로딩 시간으로도 정상 동작해야 한다', () => {
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1500,
        })
      );

      expect(result.current.isMinimumDelayActive).toBe(true);

      // 중간 시점에서는 여전히 로딩 중
      act(() => {
        jest.advanceTimersByTime(1000);
      });
      expect(result.current.isMinimumDelayActive).toBe(true);

      // 완료 시점
      act(() => {
        jest.advanceTimersByTime(500);
      });
      expect(result.current.isMinimumDelayActive).toBe(false);
      expect(result.current.isComplete).toBe(true);
    });

    it('모달이 닫히면 로딩 타이머가 정리되어야 한다', () => {
      const { result, rerender } = renderHook(
        (props) => useTimeDelayModal(props),
        {
          initialProps: {
            ...defaultProps,
            isOpen: true,
            loadingDuration: 2000,
          },
        }
      );

      expect(result.current.isMinimumDelayActive).toBe(true);

      act(() => {
        rerender({ ...defaultProps, isOpen: false });
      });

      // 훅은 isOpen이 false가 되어도 상태를 즉시 초기화하지 않음
      // 단지 타이머만 정리함
      expect(result.current.isMinimumDelayActive).toBe(true);
      expect(result.current.isComplete).toBe(false);

      // 타이머가 정리되었으므로 시간이 지나도 상태 변화 없음
      act(() => {
        jest.advanceTimersByTime(2000);
      });

      expect(result.current.isMinimumDelayActive).toBe(true);
      expect(result.current.isComplete).toBe(false);
    });
  });

  describe('자동 닫기', () => {
    it('완료 상태가 되면 자동 닫기 타이머가 시작되어야 한다', () => {
      const onCloseMock = jest.fn();
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1000,
          autoCloseDuration: 2000,
          onClose: onCloseMock,
        })
      );

      // 로딩 완료
      act(() => {
        jest.advanceTimersByTime(1000);
      });

      expect(result.current.isComplete).toBe(true);
      expect(onCloseMock).not.toHaveBeenCalled();

      // 자동 닫기 시간 경과
      act(() => {
        jest.advanceTimersByTime(2000);
      });

      expect(onCloseMock).toHaveBeenCalledTimes(1);
    });

    it('완료 상태에서 모달이 닫히면 자동 닫기 타이머가 정리되어야 한다', () => {
      const onCloseMock = jest.fn();
      const { result, rerender } = renderHook(
        (props) => useTimeDelayModal(props),
        {
          initialProps: {
            ...defaultProps,
            isOpen: true,
            loadingDuration: 1000,
            autoCloseDuration: 2000,
            onClose: onCloseMock,
          },
        }
      );

      // 로딩 완료
      act(() => {
        jest.advanceTimersByTime(1000);
      });

      expect(result.current.isComplete).toBe(true);

      // 모달 닫기
      act(() => {
        rerender({ ...defaultProps, isOpen: false, onClose: onCloseMock });
      });

      // 자동 닫기 시간이 지나도 onClose가 호출되지 않아야 함
      act(() => {
        jest.advanceTimersByTime(2000);
      });

      expect(onCloseMock).not.toHaveBeenCalled();
    });
  });

  describe('수동 닫기 (handleModalClose)', () => {
    it('로딩 중일 때 handleModalClose를 호출해도 onClose가 호출되지 않아야 한다', () => {
      const onCloseMock = jest.fn();
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          onClose: onCloseMock,
        })
      );

      expect(result.current.isMinimumDelayActive).toBe(true);

      act(() => {
        result.current.handleModalClose();
      });

      expect(onCloseMock).not.toHaveBeenCalled();
    });

    it('로딩이 완료된 후에는 handleModalClose가 다시 동작해야 한다', () => {
      const onCloseMock = jest.fn();
      const { result } = renderHook(() =>
        useTimeDelayModal({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1000,
          onClose: onCloseMock,
        })
      );

      expect(result.current.isMinimumDelayActive).toBe(true);
      act(() => {
        result.current.handleModalClose();
      });
      expect(onCloseMock).not.toHaveBeenCalled();

      // 로딩 완료
      act(() => {
        jest.advanceTimersByTime(1000);
      });

      expect(result.current.isMinimumDelayActive).toBe(false);
      expect(result.current.isComplete).toBe(true);

      act(() => {
        result.current.handleModalClose();
      });

      expect(onCloseMock).toHaveBeenCalledTimes(1);
    });
  });

  describe('상태 리셋', () => {
    it('모달이 다시 열릴 때 상태가 초기화되어야 한다', async () => {
      const onCloseMock = jest.fn();
      const { result, rerender } = renderHook(
        (props) => useTimeDelayModal(props),
        {
          initialProps: {
            ...defaultProps,
            isOpen: true,
            loadingDuration: 500,
            onClose: onCloseMock,
          },
        }
      );

      // 첫 번째 사이클 완료
      act(() => {
        jest.advanceTimersByTime(500);
      });

      await waitFor(() => {
        expect(result.current.isMinimumDelayActive).toBe(false);
        expect(result.current.isComplete).toBe(true);
      });

      // 모달 닫기
      act(() => {
        rerender({ ...defaultProps, isOpen: false, onClose: onCloseMock });
      });

      // isOpen이 false가 되어도 상태는 유지됨
      expect(result.current.isMinimumDelayActive).toBe(false);
      expect(result.current.isComplete).toBe(true);

      // 모달 다시 열기 - 이때 상태가 초기화됨
      act(() => {
        rerender({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 500,
          onClose: onCloseMock,
        });
      });

      expect(result.current.isMinimumDelayActive).toBe(true);
      expect(result.current.isComplete).toBe(false);
    });
  });

  describe('의존성 변경', () => {
    it('loadingDuration이 변경되면 새로운 시간으로 동작해야 한다', () => {
      const { result, rerender } = renderHook(
        (props) => useTimeDelayModal(props),
        {
          initialProps: {
            ...defaultProps,
            isOpen: true,
            loadingDuration: 2000,
          },
        }
      );

      expect(result.current.isMinimumDelayActive).toBe(true);

      act(() => {
        rerender({ ...defaultProps, isOpen: true, loadingDuration: 1000 });
      });

      // 원래 시간(2000ms)으로는 완료되지 않음
      act(() => {
        jest.advanceTimersByTime(1500);
      });
      expect(result.current.isMinimumDelayActive).toBe(false);
      expect(result.current.isComplete).toBe(true);
    });
  });

  describe('전체 플로우', () => {
    it('전체 플로우가 정상적으로 동작해야 한다', () => {
      const onCloseMock = jest.fn();
      const { result, rerender } = renderHook(
        (props) => useTimeDelayModal(props),
        {
          initialProps: {
            ...defaultProps,
            isOpen: false,
            loadingDuration: 1000,
            autoCloseDuration: 500,
            onClose: onCloseMock,
          },
        }
      );

      // 초기 상태
      expect(result.current.isMinimumDelayActive).toBe(false);
      expect(result.current.isComplete).toBe(false);

      // 모달 열기
      act(() => {
        rerender({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1000,
          autoCloseDuration: 500,
          onClose: onCloseMock,
        });
      });

      expect(result.current.isMinimumDelayActive).toBe(true);
      expect(result.current.isComplete).toBe(false);

      act(() => {
        result.current.handleModalClose();
      });
      expect(onCloseMock).not.toHaveBeenCalled();

      // 로딩 완료
      act(() => {
        jest.advanceTimersByTime(1000);
      });

      expect(result.current.isMinimumDelayActive).toBe(false);
      expect(result.current.isComplete).toBe(true);

      // 이제 수동 닫기 가능
      act(() => {
        result.current.handleModalClose();
      });
      expect(onCloseMock).toHaveBeenCalledTimes(1);

      // 모달 다시 열기 (상태 리셋 확인)
      onCloseMock.mockClear();
      act(() => {
        rerender({
          ...defaultProps,
          isOpen: false,
          loadingDuration: 1000,
          autoCloseDuration: 500,
          onClose: onCloseMock,
        });
      });

      // 다시 열기
      act(() => {
        rerender({
          ...defaultProps,
          isOpen: true,
          loadingDuration: 1000,
          autoCloseDuration: 500,
          onClose: onCloseMock,
        });
      });

      expect(result.current.isMinimumDelayActive).toBe(true);
      expect(result.current.isComplete).toBe(false);

      // 자동 닫기 테스트
      act(() => {
        jest.advanceTimersByTime(1000); // 로딩 완료
      });

      expect(result.current.isComplete).toBe(true);

      act(() => {
        jest.advanceTimersByTime(500); // 자동 닫기
      });

      expect(onCloseMock).toHaveBeenCalledTimes(1);
    });
  });
});
