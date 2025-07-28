import Dashboard from '@/domains/user/userDashboard/userDashboard';
import { createBrowserRouter } from 'react-router-dom';
import Suggestions from './domains/user/suggestions/Suggestions';

export const router = createBrowserRouter([
  {
    path: '/suggestion',
    element: <Suggestions />,
  },
  {
    path: '/admin',
    element: <Dashboard />,
  },
  {
    path: '/dashboard',
    element: <Dashboard />,
  },
]);
