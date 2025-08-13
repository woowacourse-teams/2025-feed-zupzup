import { initializeApp, FirebaseOptions } from 'firebase/app';

const firebaseConfig: FirebaseOptions = {
  apiKey: process.env.FIREBASE_API_KEY!,
  authDomain: process.env.FIREBASE_AUTH_DOMAIN!,
  projectId: process.env.FIREBASE_PROJECT_ID!,
  storageBucket: process.env.FIREBASE_STORAGE_BUCKET!,
  messagingSenderId: process.env.FIREBASE_MESSAGING_SENDER_ID!,
  appId: process.env.FIREBASE_APP_ID!,
};

if (
  !process.env.FIREBASE_API_KEY ||
  !process.env.FIREBASE_AUTH_DOMAIN ||
  !process.env.FIREBASE_PROJECT_ID ||
  !process.env.FIREBASE_STORAGE_BUCKET ||
  !process.env.FIREBASE_MESSAGING_SENDER_ID ||
  !process.env.FIREBASE_APP_ID
) {
  throw new Error('Missing Firebase environment variables');
}

export const firebaseApp = initializeApp(firebaseConfig);

export const VAPID_KEY = process.env.FIREBASE_VAPID_KEY!;
