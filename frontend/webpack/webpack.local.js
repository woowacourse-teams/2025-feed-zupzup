import { merge } from 'webpack-merge';
import common from './webpack.common.js';
import webpack from 'webpack';
import dotenv from 'dotenv';

import { createDefineEnv } from './buildUtils.js';

const result = dotenv.config({ path: '.env.dev' });
const env = result.parsed || {};

const defineEnv = createDefineEnv(env, 'local');

export default merge(common, {
  mode: 'development',
  devtool: 'eval-cheap-module-source-map',
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

  plugins: [new webpack.DefinePlugin(defineEnv)],
});
