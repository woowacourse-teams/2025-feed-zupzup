import { BaseEvent } from '../types';

interface UserDashboardEventFactory {
  viewSuggestionsFromDashboard: () => BaseEvent;
}

export const userDashboardEvents: UserDashboardEventFactory = {
  viewSuggestionsFromDashboard: (): BaseEvent => ({
    name: 'view_suggestions_from_dashboard',
    parameters: {
      event_category: 'engagement',
      event_label: 'user_dashboard_page',
    },
  }),
};
