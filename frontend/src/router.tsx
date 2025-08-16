import App from './App';
import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import Login from '@/domains/admin/Login/Logins';
import SignUp from '@/domains/admin/SignUp/SignUp';
import Home from '@/domains/Home';
import UserDashboard from '@/domains/user/userDashboard/UserDashboard';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import Settings from './domains/admin/Settings/Settings';
import AdminHome from './domains/admin/AdminHome/AdminHome';
import { ROUTES } from '@/constants/routes';
import ProtectedRoute from '@/utils/protectedRoute';

const isAuthenticated = true; // 로그인 api 연결하면서 수정할 예정

export const router = createBrowserRouter([
  {
    path: ROUTES.HOME,
    element: <App />,
    children: [
      {
        index: true, // "/"에 접근 시
        element: <Navigate to='/1/submit' replace />, // "/1/submit"로 리다이렉트 (임시 -> 나중에 삭제)
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
    element: (
      <ProtectedRoute isAuthenticated={isAuthenticated} redirectPath='/login' />
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
