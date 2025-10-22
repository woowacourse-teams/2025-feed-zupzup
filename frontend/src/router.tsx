import AuthRedirectRoute from '@/components/AuthRedirectRoute/AuthRedirectRoute';
import { ROUTES } from '@/constants/routes';
import { ErrorCatcher } from '@/contexts/ErrorCatcher';
import ProtectedRoute from '@/domains/components/ProtectedRoute/ProtectedRoute';
import { lazy, Suspense } from 'react';
import { createBrowserRouter } from 'react-router-dom';
import App from './App';
import AISummary from './domains/admin/AISummary/AISummary';
import GlobalErrorBoundary from './error/GlobalError/GlobalErrorBoundary';
import GlobalErrorFallback from './error/GlobalError/GlobalErrorFallback';

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
        <ErrorCatcher />
        <Suspense fallback={<div>Loading...</div>}>
          <App />
        </Suspense>
      </GlobalErrorBoundary>
    ),
    children: [
      {
        index: true,
        element: <OnBoarding />,
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
          { path: ROUTES.AI_SUMMARY, element: <AISummary /> },
        ],
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
