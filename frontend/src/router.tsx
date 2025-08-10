import App from '@/App';
import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import Home from '@/domains/Home';
import UserDashboard from '@/domains/user/userDashboard/UserDashboard';
import { createBrowserRouter } from 'react-router-dom';
import Settings from './domains/admin/Settings/Settings';
import AdminHome from './domains/admin/AdminHome/AdminHome';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        path: '',
        element: <Home />,
      },

      {
        path: '/admin',
        element: <AdminDashboard />,
      },
      {
        path: '/dashboard',
        element: <UserDashboard />,
      },
      {
        path: '/admin-settings',
        element: <Settings />,
      },
      {
        path: '/admin-home',
        element: <AdminHome />,
      },
    ],
  },
]);
