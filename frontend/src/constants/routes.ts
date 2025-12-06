export type RouteKey = keyof typeof ROUTES;
export type RouteValue = (typeof ROUTES)[RouteKey];

export const ROUTES = {
  HOME: '/',
  SUBMIT: ':id/submit',
  DASHBOARD: ':id/dashboard',
  FEEDBACK_PAGE: 'feedback',

  ADMIN: '/admin',
  ADMIN_HOME: 'home',
  ADMIN_SETTINGS: 'settings',

  LOGIN: 'login',
  SIGN_UP: 'signup',
  AI_SUMMARY: ':id/ai/summary/:clusterId',
} as const;

export const ADMIN_BASE = ROUTES.ADMIN + '/';
