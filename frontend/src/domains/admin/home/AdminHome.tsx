import Hero from '@/domains/user/home/components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import AdminFeedbackBox from './components/AdminFeedbackBox';

export default function AdminHome() {
  return (
    <section>
      <Hero
        onLoginClick={() => console.log('로그인 클릭!')}
        onSuggestClick={() => console.log('건의사항 페이지 이동')}
        title='환영합니다!'
      />
      <FeedbackBoxList>
        <AdminFeedbackBox type='incomplete' />
        <AdminFeedbackBox type='incomplete' />
        <AdminFeedbackBox type='incomplete' />
        <AdminFeedbackBox type='complete' />
        <AdminFeedbackBox type='complete' />
      </FeedbackBoxList>
    </section>
  );
}
