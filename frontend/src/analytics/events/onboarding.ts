import { CategoryListType } from '@/constants/categoryList';
import { BaseEvent } from '../types';

interface OnboardingEventFactory {
  categorySelect: (category: CategoryListType) => BaseEvent;
  viewSuggestionsFromOnboarding: () => BaseEvent;
}

export const onboardingEvents: OnboardingEventFactory = {
  categorySelect: (category: CategoryListType): BaseEvent => ({
    name: 'category_select',
    parameters: {
      event_category: 'onboarding',
      event_label: category,
    },
  }),

  viewSuggestionsFromOnboarding: (): BaseEvent => ({
    name: 'view_suggestions_from_onboarding',
    parameters: {
      event_category: 'navigation',
      event_label: 'onboarding_page',
    },
  }),
};
