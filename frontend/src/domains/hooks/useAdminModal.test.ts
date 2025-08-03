// useAdminModal.test.ts
import { renderHook, act } from '@testing-library/react';
import { useAdminModal } from './useAdminModal';
import * as adminFeedbackApi from '@/apis/adminFeedback.api';

// Mock API functions
jest.mock('@/apis/adminFeedback.api', () => ({
  deleteFeedback: jest.fn(),
  patchFeedbackStatus: jest.fn(),
}));

// 타입 캐스팅을 위한 mocked API
const mockedAdminFeedbackApi = adminFeedbackApi as jest.Mocked<
  typeof adminFeedbackApi
>;

describe('useAdminModal', () => {
  const mockOnConfirmFeedback = jest.fn();
  const mockOnDeleteFeedback = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('모달 상태 관리', () => {
    it('null 모달 상태로 초기화해야 한다', () => {
      const { result } = renderHook(() => useAdminModal());

      expect(result.current.modalState.type).toBeNull();
      expect(result.current.modalState.feedbackId).toBeUndefined();
    });

    it('올바른 피드백 ID로 확인 모달을 열어야 한다', () => {
      const { result } = renderHook(() => useAdminModal());

      act(() => {
        result.current.openFeedbackCompleteModal(1);
      });

      expect(result.current.modalState).toEqual({
        type: 'confirm',
        feedbackId: 1,
      });
    });

    it('올바른 피드백 ID로 삭제 모달을 열어야 한다', () => {
      const { result } = renderHook(() => useAdminModal());

      act(() => {
        result.current.openFeedbackDeleteModal(2);
      });

      expect(result.current.modalState).toEqual({
        type: 'delete',
        feedbackId: 2,
      });
    });

    it('모달을 닫고 상태를 초기화해야 한다', () => {
      const { result } = renderHook(() => useAdminModal());

      act(() => {
        result.current.openFeedbackCompleteModal(1);
      });

      expect(result.current.modalState.type).toBe('confirm');

      act(() => {
        result.current.closeModal();
      });

      expect(result.current.modalState.type).toBeNull();
      expect(result.current.modalState.feedbackId).toBeUndefined();
    });

    it('여러 모달 열기를 올바르게 처리해야 한다', () => {
      const { result } = renderHook(() => useAdminModal());

      act(() => {
        result.current.openFeedbackCompleteModal(1);
      });

      expect(result.current.modalState.feedbackId).toBe(1);

      act(() => {
        result.current.openFeedbackDeleteModal(2);
      });

      expect(result.current.modalState).toEqual({
        type: 'delete',
        feedbackId: 2,
      });
    });
  });

  describe('모달 액션', () => {
    it('확인 모달 액션을 성공적으로 처리해야 한다', async () => {
      mockedAdminFeedbackApi.patchFeedbackStatus.mockResolvedValue(undefined);

      const { result } = renderHook(() =>
        useAdminModal({
          onConfirmFeedback: mockOnConfirmFeedback,
          onDeleteFeedback: mockOnDeleteFeedback,
        })
      );

      act(() => {
        result.current.openFeedbackCompleteModal(1);
      });

      await act(async () => {
        await result.current.handleModalAction();
      });

      expect(mockedAdminFeedbackApi.patchFeedbackStatus).toHaveBeenCalledWith({
        feedbackId: 1,
        status: 'CONFIRMED',
      });
      expect(mockOnConfirmFeedback).toHaveBeenCalledWith(1);
      expect(result.current.modalState.type).toBeNull();
    });

    it('삭제 모달 액션을 성공적으로 처리해야 한다', async () => {
      mockedAdminFeedbackApi.deleteFeedback.mockResolvedValue(undefined);

      const { result } = renderHook(() =>
        useAdminModal({
          onConfirmFeedback: mockOnConfirmFeedback,
          onDeleteFeedback: mockOnDeleteFeedback,
        })
      );

      act(() => {
        result.current.openFeedbackDeleteModal(1);
      });

      await act(async () => {
        await result.current.handleModalAction();
      });

      expect(mockedAdminFeedbackApi.deleteFeedback).toHaveBeenCalledWith({
        feedbackId: 1,
      });
      expect(mockOnDeleteFeedback).toHaveBeenCalledWith(1);
      expect(result.current.modalState.type).toBeNull();
    });

    it('모달 상태에 feedbackId가 없으면 일찍 반환해야 한다', async () => {
      const { result } = renderHook(() => useAdminModal());

      // feedbackId 없이 모달 상태를 직접 설정
      act(() => {
        result.current.modalState.type = 'confirm';
      });

      await act(async () => {
        await result.current.handleModalAction();
      });

      expect(mockedAdminFeedbackApi.patchFeedbackStatus).not.toHaveBeenCalled();
    });

    it('콜백 함수 없이도 동작해야 한다', async () => {
      mockedAdminFeedbackApi.patchFeedbackStatus.mockResolvedValue(undefined);

      const { result } = renderHook(() => useAdminModal());

      act(() => {
        result.current.openFeedbackCompleteModal(1);
      });

      await act(async () => {
        await result.current.handleModalAction();
      });

      expect(mockedAdminFeedbackApi.patchFeedbackStatus).toHaveBeenCalledWith({
        feedbackId: 1,
        status: 'CONFIRMED',
      });
      expect(result.current.modalState.type).toBeNull();
    });
  });

  it('API 호출이 실패해도 모달을 닫아야 한다', async () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    mockedAdminFeedbackApi.patchFeedbackStatus.mockRejectedValue(
      new Error('API Error')
    );

    const { result } = renderHook(() => useAdminModal());

    act(() => {
      result.current.openFeedbackCompleteModal(1);
    });

    expect(result.current.modalState.type).toBe('confirm');

    await act(async () => {
      await result.current.handleModalAction();
    });

    expect(result.current.modalState.type).toBeNull();

    consoleErrorSpy.mockRestore();
  });
});
