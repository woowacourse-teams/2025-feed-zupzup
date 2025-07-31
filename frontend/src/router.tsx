import App from '@/App';
import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import Home from '@/domains/Home';
import UserDashboard from '@/domains/user/userDashboard/UserDashboard';
import { createBrowserRouter } from 'react-router-dom';

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
    ],
  },
]);
