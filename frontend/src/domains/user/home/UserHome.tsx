import Hero from './components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import UserFeedbackBox from './components/UserFeedbackBox/UserFeedbackBox';
import { useNavigate } from 'react-router-dom';
import { UserFeedback, FeedbackResponse } from '@/types/feedback.types';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import useGetFeedback from '@/domains/admin/home/hooks/useGetFeedback';

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
    url: '/api/places/1/feedbacks',
    key: 'feedbacks',
  });

  useGetFeedback({ fetchMore, hasNext });

  return (
    <section>
      <Hero
        onLoginClick={() => navigate('/admin')}
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
            isLiked={feedback.isLiked}
            isSecret={feedback.isSecret}
          />
        ))}
        {loading && <div>로딩중...</div>}
      </FeedbackBoxList>
      {hasNext && <div id='scroll-observer'></div>}
    </section>
  );
}
