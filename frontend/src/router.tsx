import App from './App';
import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import Home from '@/domains/Home';
import UserDashboard from '@/domains/user/userDashboard/UserDashboard';
import { createBrowserRouter } from 'react-router-dom';
import Settings from './domains/admin/Settings/Settings';
import AdminHome from './domains/admin/AdminHome/AdminHome';
import { ROUTES } from '@/constants/routes';

export const router = createBrowserRouter([
  {
    path: ROUTES.HOME,
    element: <App />,
    children: [
      {
        path: ROUTES.HOME,
        element: <Home />,
      },
      {
        path: ROUTES.ADMIN,
        element: <AdminDashboard />,
      },
      {
        path: ROUTES.USER_DASHBOARD,
        element: <UserDashboard />,
      },
      {
        path: ROUTES.ADMIN_SETTINGS,
        element: <Settings />,
      },
      {
        path: ROUTES.ADMIN_HOME,
        element: <AdminHome />,
      },
    ],
  },
]);
