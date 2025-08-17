import { ROUTES } from '@/constants/routes';
import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import Login from '@/domains/admin/Login/Login';
import SignUp from '@/domains/admin/SignUp/SignUp';
import Home from '@/domains/Home';
import UserDashboard from '@/domains/user/userDashboard/UserDashboard';
import ProtectedRoute from '@/utils/protectedRoute';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import AdminHome from './domains/admin/AdminHome/AdminHome';
import Settings from './domains/admin/Settings/Settings';
import { getAuthRedirectElement, isAuthenticated } from '@/utils/authenticated';

export const router = createBrowserRouter([
  {
    path: ROUTES.HOME,
    element: <App />,
    children: [
      {
        index: true, // "/"에 접근 시
        element: (
          <Navigate to='/d0b1b979-7ae8-11f0-8408-0242ac120002/submit' replace />
        ), // (임시 -> 나중에 삭제)
      },
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
        element: getAuthRedirectElement(<Login />),
      },
      {
        path: ROUTES.SIGN_UP,
        element: getAuthRedirectElement(<SignUp />),
      },
    ],
  },
  {
    path: ROUTES.ADMIN,
    element: (
      <ProtectedRoute
        isAuthenticated={isAuthenticated()}
        redirectPath='/login'
      />
    ),
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
