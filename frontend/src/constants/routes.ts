export type RouteKey = keyof typeof ROUTES;
export type RouteValue = (typeof ROUTES)[RouteKey];

export const ROUTES = {
  HOME: '/',
  USER_DASHBOARD: '/dashboard',
  FEEDBACK_PAGE: '/feedback',
  ONBOARDING: '/onboarding',

  ADMIN: '/admin',
  ADMIN_HOME: '/admin-home',
  ADMIN_SETTINGS: '/admin-settings',
  ADMIN_DASHBOARD: '/admin-dashboard',

  NOTIFICATIONS: '/notifications',
  SETTINGS: '/settings',
} as const;
