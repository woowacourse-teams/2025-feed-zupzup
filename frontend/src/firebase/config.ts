// src/firebase/config.ts
import { initializeApp, FirebaseOptions } from 'firebase/app';

const firebaseConfig: FirebaseOptions = {
  apiKey: 'AIzaSyC-GvMd4MkZ2T4rRHc5cKPH1KAB5yeDUL8',
  authDomain: 'feedzupzup-ba753.firebaseapp.com',
  projectId: 'feedzupzup-ba753',
  storageBucket: 'feedzupzup-ba753.firebasestorage.app',
  messagingSenderId: '1001684234371',
  appId: '1:1001684234371:web:f52cb8add92937d800abb6',
};

export const firebaseApp = initializeApp(firebaseConfig);

export const VAPID_KEY =
  'BL1Zim9mODoKpMfozMdTeI8O1sktiawgesxvRwkZz3Y8E8bQrb6NSqwTmD67uEfPJAiuafK5egdywyK8WLDSPNs'; // 실제 VAPID 키로 교체
