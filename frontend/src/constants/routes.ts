export type RouteKey = keyof typeof ROUTES;
export type RouteValue = (typeof ROUTES)[RouteKey];

export const ROUTES = {
  HOME: '/',
  SUBMIT: ':id/submit',
  USER_DASHBOARD: ':id/dashboard',
  FEEDBACK_PAGE: 'feedback',

  ADMIN: '/admin',
  ADMIN_HOME: 'home',
  ADMIN_SETTINGS: 'settings',
  ADMIN_DASHBOARD: ':id/dashboard',

  NOTIFICATIONS: 'notifications',
  SETTINGS: 'settings',

  LOGIN: 'login',
  SIGN_UP: 'signup',
} as const;
