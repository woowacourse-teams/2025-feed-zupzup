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

    // 이 테스트를 수정합니다 - ref 패턴을 사용하므로 이벤트 리스너가 재등록되지 않아야 합니다
    it('onClose가 변경되어도 이벤트 리스너를 재등록하지 않아야 한다 (ref 패턴 사용)', () => {
      const addEventListenerSpy = jest.spyOn(window, 'addEventListener');
      const removeEventListenerSpy = jest.spyOn(window, 'removeEventListener');

      const mockOnClose2 = jest.fn();

      const { rerender } = renderHook(
        ({ onClose }) => useModal({ isOpen: true, onClose }),
        { initialProps: { onClose: mockOnClose } }
      );

      // 초기 렌더링에서 한 번만 addEventListener가 호출되어야 함
      expect(addEventListenerSpy).toHaveBeenCalledTimes(1);

      rerender({ onClose: mockOnClose2 });

      // onClose가 변경되어도 removeEventListener나 추가 addEventListener가 호출되지 않아야 함
      expect(removeEventListenerSpy).not.toHaveBeenCalled();
      expect(addEventListenerSpy).toHaveBeenCalledTimes(1); // 여전히 1번만

      addEventListenerSpy.mockRestore();
      removeEventListenerSpy.mockRestore();
    });

    // 새로운 테스트 추가: ref가 실제로 최신 onClose를 참조하는지 확인
    it('onClose가 변경되면 새로운 onClose가 호출되어야 한다', () => {
      const mockOnClose2 = jest.fn();

      const { rerender } = renderHook(
        ({ onClose }) => useModal({ isOpen: true, onClose }),
        { initialProps: { onClose: mockOnClose } }
      );

      // onClose를 변경
      rerender({ onClose: mockOnClose2 });

      // Escape 키를 누르면 변경된 onClose2가 호출되어야 함
      fireEvent.keyDown(window, { key: 'Escape', code: 'Escape' });

      expect(mockOnClose).not.toHaveBeenCalled();
      expect(mockOnClose2).toHaveBeenCalledTimes(1);
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

    it('preventClose가 true일 때는 Escape 키에 반응하지 않아야 한다', () => {
      renderHook(() =>
        useModal({
          isOpen: true,
          onClose: mockOnClose,
          preventClose: true,
        })
      );

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
      const mockEvent = {
        target: contentElement,
        currentTarget: overlayElement,
      } as unknown as React.MouseEvent<HTMLDivElement>;

      act(() => {
        result.current.handleOverlayClick(mockEvent);
      });

      expect(mockOnClose).not.toHaveBeenCalled();
    });

    it('preventClose가 true일 때는 오버레이 클릭에 반응하지 않아야 한다', () => {
      const { result } = renderHook(() =>
        useModal({
          isOpen: true,
          onClose: mockOnClose,
          preventClose: true,
        })
      );

      const overlayElement = document.createElement('div');
      const mockEvent = {
        target: overlayElement,
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

      expect(typeof result.current.handleOverlayClick).toBe('function');
      expect(typeof result.current.handleConfirm).toBe('function');

      act(() => {
        result.current.handleConfirm();
      });

      expect(mockOnClose).toHaveBeenCalledTimes(1);
    });
  });
});
