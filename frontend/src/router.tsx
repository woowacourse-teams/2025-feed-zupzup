import Dashboard from '@/domains/user/dashboard/Dashboard';
import { createBrowserRouter } from 'react-router-dom';
import AdminHome from './domains/admin/home/AdminHome';
import Suggestions from './domains/user/suggestions/Suggestions';

export const router = createBrowserRouter([
  {
    path: '/suggestion',
    element: <Suggestions />,
  },
  {
    path: '/admin',
    element: <AdminHome />,
  },
  {
    path: '/dashboard',
    element: <Dashboard />,
  },
]);
