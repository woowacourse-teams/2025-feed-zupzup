import { createBrowserRouter } from 'react-router-dom';
import Suggestions from './domains/user/suggestions/Suggestions';
import AdminHome from './domains/admin/home/AdminHome';
import UserHome from './domains/user/home/UserHome';
import FeedbackInput from './domains/user/home/components/FeedbackInput/FeedbackInput';

export const router = createBrowserRouter([
  // {
  //   path: '/',
  //   element: <UserHome />,
  // },
  {
    path: '/',
    element: <FeedbackInput />,
  },
  {
    path: '/suggestion',
    element: <Suggestions />,
  },
  {
    path: '/admin',
    element: <AdminHome />,
  },
]);
