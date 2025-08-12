import App from '@/App';
import AdminDashboard from '@/domains/admin/adminDashboard/AdminDashboard';
import Login from '@/domains/admin/login/Login';
import SignUp from '@/domains/admin/signUp/SignUp';
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
        path: '/login',
        element: <Login />,
      },
      {
        path: '/signup',
        element: <SignUp />,
      },
      {
        path: '/dashboard',
        element: <UserDashboard />,
      },
    ],
  },
]);
