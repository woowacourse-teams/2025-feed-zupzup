import AdminFeedbackBox from './domains/admin/home/components/AdminFeedbackBox';
import UserFeedbackBox from './domains/user/home/components/UserFeedbackBox/UserFeedbackBox';

export default function App() {
  return (
    <p>
      <AdminFeedbackBox type='CONFIRMED' />
      <AdminFeedbackBox type='WAITING' />
      <UserFeedbackBox type='CONFIRMED' />
      <UserFeedbackBox type='WAITING' />
    </p>
  );
}
