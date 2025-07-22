import UserFeedbackBox from './domains/user/home/components/UserFeedbackBox/UserFeedbackBox';

export default function App() {
  return (
    <p>
      <UserFeedbackBox type='complete' />
      <UserFeedbackBox type='incomplete' />
    </p>
  );
}
