import { BaseEvent } from '../types';

interface SuggestionFormEventFactory {
  randomNicknameClick: () => BaseEvent;
  privacyToggle: (isEnabled: boolean) => BaseEvent;
  formSubmit: () => BaseEvent;
  viewSuggestionsFromForm: () => BaseEvent;
}

export const suggestionFormEvents: SuggestionFormEventFactory = {
  randomNicknameClick: (): BaseEvent => ({
    name: 'random_nickname_click',
    parameters: {
      event_category: 'user_interaction',
      event_label: 'suggestion_form',
    },
  }),

  privacyToggle: (isEnabled: boolean): BaseEvent => ({
    name: 'privacy_toggle',
    parameters: {
      event_category: 'user_interaction',
      event_label: isEnabled ? 'enabled' : 'disabled',
    },
  }),

  formSubmit: (): BaseEvent => ({
    name: 'feedback_submit',
    parameters: {
      event_category: 'conversion',
      event_label: 'suggestion_submitted',
      value: 1,
    },
  }),

  viewSuggestionsFromForm: (): BaseEvent => ({
    name: 'view_suggestions_from_form',
    parameters: {
      event_category: 'navigation',
      event_label: 'suggestion_form',
    },
  }),
};
