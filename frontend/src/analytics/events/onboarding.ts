import { BaseEvent } from '../types';

type CategoryType = '시설' | '학사행정' | '커리큘럼' | '기타';

interface OnboardingEventFactory {
  categorySelect: (category: CategoryType) => BaseEvent;
  viewSuggestionsFromOnboarding: () => BaseEvent;
}

export const onboardingEvents: OnboardingEventFactory = {
  categorySelect: (category: CategoryType): BaseEvent => ({
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
