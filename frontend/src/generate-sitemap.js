/* eslint-env node */

import { SitemapStream, streamToPromise } from 'sitemap';
import { createWriteStream, mkdirSync, existsSync } from 'fs';
import path from 'path';

const BASE_URL = 'https://feedzupzup.com';

const OUT_PATH = path.join(process.cwd(), 'dist');

export const ROUTES = {
  HOME: '/',
  SUBMIT: ':id/submit',
  DASHBOARD: ':id/dashboard',
  FEEDBACK_PAGE: 'feedback',
  ADMIN: '/admin',
  ADMIN_HOME: 'home',
  ADMIN_SETTINGS: 'settings',
  LOGIN: 'login',
  SIGN_UP: 'signup',
  AI_SUMMARY: ':id/ai/summary/:clusterId',
};

const INCLUDE_STATIC_KEYS = new Set(['HOME']);

const EXCLUDE_KEYS = new Set([
  'ADMIN',
  'ADMIN_HOME',
  'ADMIN_SETTINGS',
  'LOGIN',
  'SIGN_UP',
  'SUBMIT',
  'DASHBOARD',
  'AI_SUMMARY',
  'FEEDBACK_PAGE',
]);

function isStaticPath(value) {
  return !value.includes(':');
}
function normalize(path) {
  return path.startsWith('/') ? path : `/${path}`;
}

function getStaticPublicPathsFromRoutes() {
  const paths = [];
  for (const [key, value] of Object.entries(ROUTES)) {
    if (EXCLUDE_KEYS.has(key)) continue;
    if (!isStaticPath(value)) continue;
    if (INCLUDE_STATIC_KEYS.size && !INCLUDE_STATIC_KEYS.has(key)) continue;
    paths.push(normalize(value));
  }
  return paths;
}

async function main() {
  if (!existsSync(OUT_PATH)) mkdirSync(OUT_PATH, { recursive: true });

  const smStream = new SitemapStream({ hostname: BASE_URL });
  const writeStream = createWriteStream(path.join(OUT_PATH, 'sitemap.xml'));
  smStream.pipe(writeStream);

  const now = new Date().toISOString();

  const staticPaths = getStaticPublicPathsFromRoutes();
  for (const url of staticPaths) {
    smStream.write({
      url,
      changefreq: 'daily',
      priority: 1.0,
      lastmod: now,
    });
  }

  smStream.end();
  await streamToPromise(smStream);
  console.log('✅ sitemap.xml generated (only /)');
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
