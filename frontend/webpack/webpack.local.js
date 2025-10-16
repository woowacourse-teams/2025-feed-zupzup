import { merge } from 'webpack-merge';
import common from './webpack.common.js';
import webpack from 'webpack';
import { InjectManifest } from 'workbox-webpack-plugin';
import path, { dirname } from 'path';
import { fileURLToPath } from 'url';
import dotenv from 'dotenv';
import { createDefineEnv } from './buildUtils.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const result = dotenv.config({ path: '.env.dev' });
const env = result.parsed || {};

const defineEnv = createDefineEnv(env, 'local');
const enableServiceWorker = env.ENABLE_SW === 'true';

export default merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',

  output: {
    filename: '[name].js',
    chunkFilename: '[name].js',
    assetModuleFilename: 'assets/[name][ext]',
  },

  devServer: {
    static: './dist',
    host: '0.0.0.0',
    port: 3000,
    allowedHosts: 'all',
    open: {
      app: {
        name: 'google chrome',
      },
    },

    hot: true,
    historyApiFallback: true,
    proxy: [
      {
        context: ['/api'],
        target: env.BASE_URL,
        changeOrigin: true,
        secure: true,
        logLevel: 'debug',
        pathRewrite: {
          '^/api': '',
        },
        onProxyReq: (proxyReq) => {
          proxyReq.setHeader('Origin', env.BASE_URL);
        },
        onProxyRes: (proxyRes) => {
          proxyRes.headers['Access-Control-Allow-Origin'] =
            'http://localhost:3000';
          proxyRes.headers['Access-Control-Allow-Credentials'] = 'true';

          const setCookie = proxyRes.headers['set-cookie'];
          if (setCookie) {
            proxyRes.headers['set-cookie'] = setCookie.map((cookie) => {
              return cookie
                .replace(/; Secure(?!=false)/g, '')
                .replace(/; Secure=false/g, '')
                .replace(/SameSite=None/g, 'SameSite=Lax')
                .replace(/; Domain=[^;]+/gi, '');
            });
          }
        },
      },
    ],
  },

  plugins: [
    new webpack.DefinePlugin(defineEnv),
    ...(enableServiceWorker
      ? [
          new InjectManifest({
            swSrc: path.resolve(__dirname, '../public/service-worker.js'),
            swDest: 'service-worker.js',
            exclude: [
              /\.map$/,
              /manifest$/,
              /\.htaccess$/,
              /service-worker\.js$/,
              /mockServiceWorker\.js$/,
              /\.hot-update/,
              /analyze/,
              /bundle-report/,
            ],
            maximumFileSizeToCacheInBytes: 5 * 1024 * 1024,
            mode: 'development',
          }),
        ]
      : [
          {
            apply: (compiler) => {
              compiler.hooks.emit.tapAsync(
                'CreateDummySW',
                (compilation, callback) => {
                  const dummySW = `
console.log('[SW Dev] Service Worker disabled for faster development');
self.addEventListener('install', () => self.skipWaiting());
self.addEventListener('activate', () => self.clients.claim());
`;
                  compilation.assets['service-worker.js'] = {
                    source: () => dummySW,
                    size: () => dummySW.length,
                  };
                  callback();
                }
              );
            },
          },
        ]),
  ],
});
