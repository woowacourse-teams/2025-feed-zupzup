import Hero from './components/Hero/Hero';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import UserFeedbackBox from './components/UserFeedbackBox/UserFeedbackBox';

export default function UserHome() {
  return (
    <section>
      <Hero
        onLoginClick={() => console.log('로그인 클릭!')}
        onSuggestClick={() => console.log('건의사항 페이지 이동')}
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
