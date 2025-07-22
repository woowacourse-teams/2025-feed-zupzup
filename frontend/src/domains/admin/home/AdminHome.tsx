import Hero from '@/domains/user/home/components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import AdminFeedbackBox from './components/AdminFeedbackBox';
import { useNavigate } from 'react-router-dom';

export default function AdminHome() {
  const navigate = useNavigate();
  return (
    <section>
      <Hero
        onLoginClick={() => navigate('/')}
        onSuggestClick={() => navigate('/suggestion')}
        title='환영합니다!'
        showSuggestButton={false}
      />
      <FeedbackBoxList>
        <AdminFeedbackBox type='WAITING' />
        <AdminFeedbackBox type='WAITING' />
        <AdminFeedbackBox type='WAITING' />
        <AdminFeedbackBox type='CONFIRMED' />
        <AdminFeedbackBox type='CONFIRMED' />
      </FeedbackBoxList>
    </section>
  );
}
