import Hero from './components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import UserFeedbackBox from './components/UserFeedbackBox/UserFeedbackBox';
import { useNavigate } from 'react-router-dom';
import { UserFeedback, FeedbackResponse } from '@/types/feedback.types';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import useGetFeedback from '@/domains/admin/home/hooks/useGetFeedback';
import { getLocalStorage } from '@/utils/localStorage';
import { useEffect, useState } from 'react';
import { css, keyframes } from '@emotion/react';

export default function UserHome() {
  const navigate = useNavigate();

  const {
    items: feedbacks,
    fetchMore,
    hasNext,
    loading,
  } = useInfinityScroll<
    UserFeedback,
    'feedbacks',
    FeedbackResponse<UserFeedback>
  >({
    url: '/places/1/feedbacks',
    key: 'feedbacks',
  });

  useGetFeedback({ fetchMore, hasNext, loading });

  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];

  const storageHighlightedId = localStorage.getItem('highlightedId');

  const [highlightedId, setHighLightedId] = useState<number | null>(null);

  useEffect(() => {
    if (!storageHighlightedId) return;

    setHighLightedId(Number(storageHighlightedId));

    const timeout = setTimeout(() => {
      setHighLightedId(null);
      localStorage.removeItem('highlightedId');
    }, 2000);

    return () => clearTimeout(timeout);
  }, []);
  return (
    <section>
      <Hero
        onSuggestClick={() => navigate('/suggestion')}
        title='우테코'
        isUserPage={true}
      />
      <FeedbackBoxList>
        {feedbacks.map((feedback: UserFeedback) => (
          <UserFeedbackBox
            key={feedback.feedbackId}
            type={feedback.status}
            content={feedback.content}
            createdAt={feedback.createdAt}
            isLiked={getFeedbackIsLike(likedFeedbackIds, feedback.feedbackId)}
            isSecret={feedback.isSecret}
            feedbackId={feedback.feedbackId}
            likeCount={feedback.likeCount}
            customCSS={
              feedback.feedbackId === highlightedId ? highlightStyle : null
            }
          />
        ))}
        {loading && <div>로딩중...</div>}
      </FeedbackBoxList>
      {hasNext && !loading && <div id='scroll-observer'></div>}
    </section>
  );
}

function getFeedbackIsLike(likedFeedbackIds: number[], feedbackId: number) {
  const isLiked = likedFeedbackIds?.includes(feedbackId);

  return !!isLiked;
}

export const highlightFade = keyframes`
  0% {
    background-color: #f0ede6;
  }
  100% {
    background-color: white;

  }
`;

export const highlightStyle = css`
  border-radius: 12px;
  animation: ${highlightFade} 2s linear;
`;
