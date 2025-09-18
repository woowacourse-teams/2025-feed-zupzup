import { FEEDBACK_FORM_CONSTANTS } from '../constants/FeedbackForm';

export function generateRandomUsername(): string {
  const randomAdjective =
    FEEDBACK_FORM_CONSTANTS.ADJECTIVES[
      Math.floor(Math.random() * FEEDBACK_FORM_CONSTANTS.ADJECTIVES.length)
    ];
  const randomAnimal =
    FEEDBACK_FORM_CONSTANTS.ANIMALS[
      Math.floor(Math.random() * FEEDBACK_FORM_CONSTANTS.ANIMALS.length)
    ];
  return `${randomAdjective} ${randomAnimal}`;
}
