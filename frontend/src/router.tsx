import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import { createBrowserRouter } from 'react-router-dom';
import Suggestions from './domains/user/suggestions/Suggestions';
import UserDashboard from '@/domains/user/userDashboard/UserDashboard';

export const router = createBrowserRouter([
  // {
  //   path: '/',
  //   element: <UserHome />,
  // },
  {
    path: '/suggestion',
    element: <Suggestions />,
  },
  {
    path: '/admin',
    element: <AdminDashboard />,
  },
  {
    path: '/dashboard',
    element: <UserDashboard />,
  },
]);
