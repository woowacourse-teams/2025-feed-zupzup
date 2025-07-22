import Hero from './components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import UserFeedbackBox from './components/UserFeedbackBox/UserFeedbackBox';
import { useNavigate } from 'react-router-dom';

// interface Feedback {
//   feedbackId: number;
//   content: string;
//   status: 'WAITING' | 'CONFIRMED';
//   isSecret: boolean;
//   isLiked: boolean;
//   createdAt: string; // ISO 형식 날짜 문자열
// }

export default function UserHome() {
  const navigate = useNavigate();

  // const {
  //   items: feedbacks,
  //   fetchMore,
  //   hasNext,
  //   loading,
  // } = useInfinityScroll<Feedback, 'feedbacks', FeedbackResponse>({
  //   url: '/api/places/1/feedbacks',
  //   key: 'feedbacks',
  // });

  return (
    <section>
      <Hero
        onLoginClick={() => navigate('/admin')}
        onSuggestClick={() => navigate('/suggestion')}
        title='환영합니다!'
        showSuggestButton={true}
      />
      <FeedbackBoxList>
        <UserFeedbackBox type='WAITING' />
        <UserFeedbackBox type='WAITING' />
        <UserFeedbackBox type='WAITING' />
        <UserFeedbackBox type='CONFIRMED' />
        <UserFeedbackBox type='CONFIRMED' />
      </FeedbackBoxList>
    </section>
  );
}
