import { ValidationState } from '@/types/validateState.types';

export const mustMatch =
  (regex: RegExp, message: string) =>
  (value: string): ValidationState => {
    return regex.test(value) ? { ok: true } : { ok: false, message };
  };

export const mustNotMatch =
  (regex: RegExp, message: string) =>
  (value: string): ValidationState => {
    return regex.test(value) ? { ok: false, message } : { ok: true };
  };

export const lengthBetween =
  (min: number, max: number, message: string) =>
  (value: string): ValidationState => {
    const len = value.length;
    return len < min || len > max ? { ok: false, message } : { ok: true };
  };

export const lengthAtLeast =
  (min: number, message: string) =>
  (value: string): ValidationState =>
    value.length >= min ? { ok: true } : { ok: false, message };

export const lengthAtMost =
  (max: number, message: string) =>
  (value: string): ValidationState =>
    value.length <= max ? { ok: true } : { ok: false, message };
