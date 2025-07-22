import Hero from './components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import UserFeedbackBox from './components/UserFeedbackBox/UserFeedbackBox';
import { useNavigate } from 'react-router-dom';

export default function UserHome() {
  const navigate = useNavigate();
  return (
    <section>
      <Hero
        onLoginClick={() => navigate('/admin')}
        onSuggestClick={() => navigate('/suggestion')}
        title='환영합니다!'
      />
      <FeedbackBoxList>
        <UserFeedbackBox type='incomplete' />
        <UserFeedbackBox type='incomplete' />
        <UserFeedbackBox type='incomplete' />
        <UserFeedbackBox type='complete' />
        <UserFeedbackBox type='complete' />
      </FeedbackBoxList>
    </section>
  );
}
