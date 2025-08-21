import { getMessaging, Messaging } from 'firebase/messaging';
import { firebaseApp } from './config';

let messaging: Messaging | null = null;

if (typeof window !== 'undefined' && 'serviceWorker' in navigator) {
  try {
    messaging = getMessaging(firebaseApp);
  } catch (error) {
    console.error('Firebase messaging 초기화 실패:', error);
  }
}

export { messaging };
