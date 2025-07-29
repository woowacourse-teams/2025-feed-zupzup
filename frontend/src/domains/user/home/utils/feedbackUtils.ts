import { FEEDBACK_INPUT_CONSTANTS } from '../constants/feedbackInput';

export function generateRandomUsername(): string {
  const randomAdjective =
    FEEDBACK_INPUT_CONSTANTS.ADJECTIVES[
      Math.floor(Math.random() * FEEDBACK_INPUT_CONSTANTS.ADJECTIVES.length)
    ];
  const randomAnimal =
    FEEDBACK_INPUT_CONSTANTS.ANIMALS[
      Math.floor(Math.random() * FEEDBACK_INPUT_CONSTANTS.ANIMALS.length)
    ];
  return `${randomAdjective} ${randomAnimal}`;
}

export function generateRandomAvatar(): string {
  return FEEDBACK_INPUT_CONSTANTS.AVATARS[
    Math.floor(Math.random() * FEEDBACK_INPUT_CONSTANTS.AVATARS.length)
  ] as (typeof FEEDBACK_INPUT_CONSTANTS.AVATARS)[number];
}
