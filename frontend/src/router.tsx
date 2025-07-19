import { createBrowserRouter } from 'react-router-dom';
import Suggestions from './domains/user/suggestions/Suggestions';
import App from './App';

export const router = createBrowserRouter([
  {
    path: '/root',
    element: <App />,
  },
  {
    path: '/suggestion',
    element: <Suggestions />,
  },
  // {
  //   path: '/admin',
  //   element: <App />,
  // },
]);
