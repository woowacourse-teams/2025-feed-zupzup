import { lazy, Suspense } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { ADMIN_BASE, ROUTES } from '@/constants/routes';
import App from './App';
import AuthRedirectRoute from '@/components/AuthRedirectRoute/AuthRedirectRoute';
import ProtectedRoute from '@/domains/components/ProtectedRoute/ProtectedRoute';
import { isAuthenticated } from './utils/isAuthenticated';
import GlobalErrorBoundary from './error/GlobalErrorBoundary';
import GlobalErrorFallback from './error/GlobalErrorFallback';

const AdminDashboard = lazy(
  () =>
    import(
      /* webpackChunkName: "admin-dashboard" */ '@/domains/admin/adminDashboard/AdminDashboard'
    )
);
const Login = lazy(
  () => import(/* webpackChunkName: "login" */ '@/domains/admin/Login/Login')
);
const SignUp = lazy(
  () => import(/* webpackChunkName: "signup" */ '@/domains/admin/SignUp/SignUp')
);
const Home = lazy(
  () => import(/* webpackChunkName: "home" */ '@/domains/Home')
);
const UserDashboard = lazy(
  () =>
    import(
      /* webpackChunkName: "user-dashboard" */ '@/domains/user/userDashboard/UserDashboard'
    )
);
const AdminHome = lazy(
  () =>
    import(
      /* webpackChunkName: "admin-home" */ './domains/admin/AdminHome/AdminHome'
    )
);
const Settings = lazy(
  () =>
    import(
      /* webpackChunkName: "admin-settings" */ './domains/admin/Settings/Settings'
    )
);
const NotFoundPage = lazy(
  () =>
    import(
      /* webpackChunkName: "not-found" */ './components/NotFoundPage/NotFoundPage'
    )
);

const OnBoarding = lazy(
  () =>
    import(
      /* webpackChunkName: "onboarding" */ './domains/admin/OnBoarding/OnBoarding'
    )
);

export const router = createBrowserRouter([
  {
    path: ROUTES.HOME,
    element: (
      <GlobalErrorBoundary fallback={GlobalErrorFallback}>
        <Suspense fallback={<div>Loading...</div>}>
          <App />
        </Suspense>
      </GlobalErrorBoundary>
    ),
    children: [
      {
        index: true,
        element: isAuthenticated() ? (
          <Navigate to={ADMIN_BASE + ROUTES.ADMIN_HOME} replace />
        ) : (
          <OnBoarding />
        ),
      },
      { path: ROUTES.SUBMIT, element: <Home /> },
      { path: ROUTES.DASHBOARD, element: <UserDashboard /> },
      {
        path: ROUTES.LOGIN,
        element: (
          <AuthRedirectRoute>
            <Login />
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
          { path: ROUTES.DASHBOARD, element: <AdminDashboard /> },
          { path: ROUTES.ADMIN_SETTINGS, element: <Settings /> },
        ],
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
