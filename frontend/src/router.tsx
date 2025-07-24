import { createBrowserRouter } from 'react-router-dom';
import Suggestions from './domains/user/suggestions/Suggestions';
import AdminHome from './domains/admin/home/AdminHome';
import UserHome from './domains/user/home/UserHome';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <UserHome />,
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
