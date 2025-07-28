import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import UserDashboard from '@/domains/user/userDashboard/UserDashboard';
import { createBrowserRouter } from 'react-router-dom';
import Suggestions from './domains/user/suggestions/Suggestions';

export const router = createBrowserRouter([
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
