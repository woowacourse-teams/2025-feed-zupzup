import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import SignUp from '@/domains/admin/SignUp/SignUp';
import Home from '@/domains/Home';
import UserDashboard from '@/domains/user/userDashboard/UserDashboard';
import AuthRedirectRoute from '@/components/AuthRedirectRoute/AuthRedirectRoute';
import ProtectedRoute from '@/domains/components/ProtectedRoute/ProtectedRoute';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import AdminHome from './domains/admin/AdminHome/AdminHome';
import Settings from './domains/admin/Settings/Settings';
import NotFoundPage from './components/NotFoundPage/NotFoundPage';
import { isAuthenticated } from './utils/isAuthenticated';
import Login from './domains/admin/Login/Login';
import OnBoarding from './domains/admin/OnBoarding/OnBoarding';

export const router = createBrowserRouter([
  {
    path: ROUTES.HOME,
    element: <App />,
    children: [
      {
        index: true,
        element: isAuthenticated() ? (
          <Navigate to={ADMIN_BASE + ROUTES.ADMIN_HOME} replace />
        ) : (
          <OnBoarding />
        ),
      },
      {
        path: ROUTES.SUBMIT,
        element: <Home />,
      },
      {
        path: ROUTES.DASHBOARD,
        element: <UserDashboard />,
      },
      {
        path: ROUTES.LOGIN,
        element: (
          <AuthRedirectRoute>
            <Login />
            {/* <OnBoarding /> */}
          </AuthRedirectRoute>
        ),
      },
      {
        path: ROUTES.SIGN_UP,
        element: (
          <AuthRedirectRoute>
            <SignUp />
          </AuthRedirectRoute>
        ),
      },
      {
        path: ROUTES.ADMIN,
        element: <ProtectedRoute redirectPath='/login' />,
        children: [
          {
            path: ROUTES.ADMIN_HOME,
            element: <AdminHome />,
          },
          {
            path: ROUTES.DASHBOARD,
            element: <AdminDashboard />,
          },
          {
            path: ROUTES.ADMIN_SETTINGS,
            element: <Settings />,
          },
        ],
      },
      {
        path: '*',
        element: <NotFoundPage />,
      },
    ],
  },
]);
