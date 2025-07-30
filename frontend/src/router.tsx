import { createBrowserRouter } from 'react-router-dom';
import Suggestions from './domains/user/suggestions/Suggestions';
import AdminHome from './domains/admin/home/AdminHome';
import Home from '@/domains/Home';
import App from '@/App';

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
        path: 'suggestion',
        element: <Suggestions />,
      },
      {
        path: 'admin',
        element: <AdminHome />,
      },
    ],
  },
]);
