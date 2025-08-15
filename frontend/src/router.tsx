import App from './App';
import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import Login from '@/domains/admin/Login/Logins';
import SignUp from '@/domains/admin/SignUp/SignUp';
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
        path: ROUTES.SUBMIT,
        element: <Home />,
      },
      {
        path: ROUTES.USER_DASHBOARD,
        element: <UserDashboard />,
      },
      {
        path: ROUTES.LOGIN,
        element: <Login />,
      },
      {
        path: ROUTES.SIGN_UP,
        element: <SignUp />,
      },
    ],
  },
  {
    path: ROUTES.ADMIN,
    element: <App />,
    children: [
      {
        path: ROUTES.ADMIN_HOME,
        element: <AdminHome />,
      },
      {
        path: ROUTES.ADMIN_DASHBOARD,
        element: <AdminDashboard />,
      },
      {
        path: ROUTES.ADMIN_SETTINGS,
        element: <Settings />,
      },
    ],
  },
]);
