import { renderHook, act } from '@testing-library/react';
import { fireEvent } from '@testing-library/react';
import { useModal } from './useModal';

describe('useModal', () => {
  const mockOnClose = jest.fn();
  const mockOnConfirm = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('이벤트 리스너 관리', () => {
    it('모달이 닫혀있을 때 이벤트 리스너를 추가하지 않아야 한다', () => {
      const addEventListenerSpy = jest.spyOn(window, 'addEventListener');

      renderHook(() => useModal({ isOpen: false, onClose: mockOnClose }));

      expect(addEventListenerSpy).not.toHaveBeenCalled();

      addEventListenerSpy.mockRestore();
    });

    it('모달이 열릴 때 이벤트 리스너를 추가해야 한다', () => {
      const addEventListenerSpy = jest.spyOn(window, 'addEventListener');

      renderHook(() => useModal({ isOpen: true, onClose: mockOnClose }));

      expect(addEventListenerSpy).toHaveBeenCalledWith(
        'keydown',
        expect.any(Function)
      );

      addEventListenerSpy.mockRestore();
    });

    it('모달이 닫힐 때 이벤트 리스너를 제거해야 한다', () => {
      const removeEventListenerSpy = jest.spyOn(window, 'removeEventListener');

      const { rerender } = renderHook(
        ({ isOpen }) => useModal({ isOpen, onClose: mockOnClose }),
        { initialProps: { isOpen: true } }
      );

      rerender({ isOpen: false });

      expect(removeEventListenerSpy).toHaveBeenCalledWith(
        'keydown',
        expect.any(Function)
      );

      removeEventListenerSpy.mockRestore();
    });

    it('언마운트 시 이벤트 리스너를 제거해야 한다', () => {
      const removeEventListenerSpy = jest.spyOn(window, 'removeEventListener');

      const { unmount } = renderHook(() =>
        useModal({ isOpen: true, onClose: mockOnClose })
      );

      unmount();

      expect(removeEventListenerSpy).toHaveBeenCalledWith(
        'keydown',
        expect.any(Function)
      );

      removeEventListenerSpy.mockRestore();
    });

    it('의존성이 변경될 때 이벤트 리스너를 업데이트해야 한다', () => {
      const addEventListenerSpy = jest.spyOn(window, 'addEventListener');
      const removeEventListenerSpy = jest.spyOn(window, 'removeEventListener');

      const mockOnClose2 = jest.fn();

      const { rerender } = renderHook(
        ({ onClose }) => useModal({ isOpen: true, onClose }),
        { initialProps: { onClose: mockOnClose } }
      );

      rerender({ onClose: mockOnClose2 });

      expect(removeEventListenerSpy).toHaveBeenCalled();
      expect(addEventListenerSpy).toHaveBeenCalledTimes(2);

      addEventListenerSpy.mockRestore();
      removeEventListenerSpy.mockRestore();
    });
  });

  describe('키보드 상호작용', () => {
    it('Escape 키를 누르면 onClose를 호출해야 한다', () => {
      renderHook(() => useModal({ isOpen: true, onClose: mockOnClose }));

      fireEvent.keyDown(window, { key: 'Escape', code: 'Escape' });

      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    it('다른 키를 누르면 onClose를 호출하지 않아야 한다', () => {
      renderHook(() => useModal({ isOpen: true, onClose: mockOnClose }));

      fireEvent.keyDown(window, { key: 'Enter', code: 'Enter' });
      fireEvent.keyDown(window, { key: 'Space', code: 'Space' });
      fireEvent.keyDown(window, { key: 'Tab', code: 'Tab' });

      expect(mockOnClose).not.toHaveBeenCalled();
    });

    it('모달이 닫혀있을 때는 키보드 이벤트에 반응하지 않아야 한다', () => {
      renderHook(() => useModal({ isOpen: false, onClose: mockOnClose }));

      fireEvent.keyDown(window, { key: 'Escape', code: 'Escape' });

      expect(mockOnClose).not.toHaveBeenCalled();
    });
  });

  describe('오버레이 클릭 처리', () => {
    it('오버레이를 클릭할 때 onClose를 호출해야 한다 (target === currentTarget)', () => {
      const { result } = renderHook(() =>
        useModal({ isOpen: true, onClose: mockOnClose })
      );

      const overlayElement = document.createElement('div');
      // 오버레이 클릭을 시뮬레이션 (target과 currentTarget이 동일)
      const mockEvent = {
        target: overlayElement,
        currentTarget: overlayElement,
      } as unknown as React.MouseEvent<HTMLDivElement>;

      act(() => {
        result.current.handleOverlayClick(mockEvent);
      });

      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    it('모달 내부를 클릭할 때 onClose를 호출하지 않아야 한다 (target !== currentTarget)', () => {
      const { result } = renderHook(() =>
        useModal({ isOpen: true, onClose: mockOnClose })
      );

      const overlayElement = document.createElement('div');
      const contentElement = document.createElement('div');
      // 모달 내부 클릭을 시뮬레이션 (target과 currentTarget이 다름)
      const mockEvent = {
        target: contentElement,
        currentTarget: overlayElement,
      } as unknown as React.MouseEvent<HTMLDivElement>;

      act(() => {
        result.current.handleOverlayClick(mockEvent);
      });

      expect(mockOnClose).not.toHaveBeenCalled();
    });
  });

  describe('확인 액션 처리', () => {
    it('handleConfirm이 호출되면 onConfirm과 onClose를 모두 호출해야 한다', () => {
      const { result } = renderHook(() =>
        useModal({
          isOpen: true,
          onClose: mockOnClose,
          onConfirm: mockOnConfirm,
        })
      );

      act(() => {
        result.current.handleConfirm();
      });

      expect(mockOnConfirm).toHaveBeenCalledTimes(1);
      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    it('onConfirm 콜백이 제공되지 않았을 때는 onClose만 호출해야 한다', () => {
      const { result } = renderHook(() =>
        useModal({ isOpen: true, onClose: mockOnClose })
      );

      act(() => {
        result.current.handleConfirm();
      });

      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    it('onConfirm을 onClose보다 먼저 호출해야 한다', () => {
      const callOrder: string[] = [];
      const mockOnConfirmWithOrder = jest.fn(() => callOrder.push('confirm'));
      const mockOnCloseWithOrder = jest.fn(() => callOrder.push('close'));

      const { result } = renderHook(() =>
        useModal({
          isOpen: true,
          onClose: mockOnCloseWithOrder,
          onConfirm: mockOnConfirmWithOrder,
        })
      );

      act(() => {
        result.current.handleConfirm();
      });

      expect(callOrder).toEqual(['confirm', 'close']);
    });
  });

  describe('반환값', () => {
    it('handleOverlayClick과 handleConfirm 함수를 반환해야 한다', () => {
      const { result } = renderHook(() =>
        useModal({ isOpen: true, onClose: mockOnClose })
      );

      expect(typeof result.current.handleOverlayClick).toBe('function');
      expect(typeof result.current.handleConfirm).toBe('function');
    });

    it('함수들이 정상적으로 동작해야 한다', () => {
      const { result, rerender } = renderHook(() =>
        useModal({ isOpen: true, onClose: mockOnClose })
      );

      rerender();

      // 함수 참조는 매번 새로 생성되지만, 기능은 정상 동작해야 함
      expect(typeof result.current.handleOverlayClick).toBe('function');
      expect(typeof result.current.handleConfirm).toBe('function');

      // 실제로 함수가 동작하는지 확인
      act(() => {
        result.current.handleConfirm();
      });

      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });
  });
});
