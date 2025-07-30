import { renderHook, act } from '@testing-library/react';
import useFeedbackManagement from './useFeedbackManagement';
import { AdminFeedback } from '@/types/feedback.types';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';

const mockFeedbacks: AdminFeedback[] = [
  {
    feedbackId: 1,
    content: 'Test feedback 1',
    status: 'PENDING' as FeedbackStatusType,
    isSecret: false,
    createdAt: '2024-01-01T00:00:00Z',
    userName: 'testuser1',
    likeCount: 5,
    imageUrl: null,
  },
  {
    feedbackId: 2,
    content: 'Test feedback 2',
    status: 'PENDING' as FeedbackStatusType,
    isSecret: true,
    createdAt: '2024-01-02T00:00:00Z',
    userName: 'testuser2',
    likeCount: 3,
    imageUrl: 'https://example.com/image.jpg',
  },
];

jest.setTimeout(10000);

describe('useFeedbackManagement', () => {
  it('초기 피드백 데이터로 상태를 초기화해야 한다', () => {
    const { result } = renderHook(() => {
      return useFeedbackManagement({ originalFeedbacks: mockFeedbacks });
    });

    expect(result.current.feedbacks).toEqual(mockFeedbacks);
  });

  it('원본 피드백이 변경되면 상태를 업데이트해야 한다', () => {
    const { result, rerender } = renderHook(
      ({ originalFeedbacks }) => useFeedbackManagement({ originalFeedbacks }),
      { initialProps: { originalFeedbacks: mockFeedbacks } }
    );

    const newFeedbacks: AdminFeedback[] = [
      ...mockFeedbacks,
      {
        feedbackId: 3,
        content: 'Test feedback 3',
        status: 'PENDING' as FeedbackStatusType,
        isSecret: false,
        createdAt: '2024-01-03T00:00:00Z',
        userName: 'testuser3',
        likeCount: 1,
        imageUrl: null,
      },
    ];

    rerender({ originalFeedbacks: newFeedbacks });

    expect(result.current.feedbacks).toEqual(newFeedbacks);
  });

  it('피드백을 확인하고 상태를 CONFIRMED로 업데이트해야 한다', () => {
    const { result } = renderHook(() =>
      useFeedbackManagement({ originalFeedbacks: mockFeedbacks })
    );

    act(() => {
      result.current.confirmFeedback(1);
    });

    expect(result.current.feedbacks[0].status).toBe('CONFIRMED');
    expect(result.current.feedbacks[1].status).toBe('PENDING');
  });
});
