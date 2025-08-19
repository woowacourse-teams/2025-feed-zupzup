import dotenv from 'dotenv';
import webpack from 'webpack';
import { merge } from 'webpack-merge';
import common from './webpack.common.js';
import { createDefineEnv } from './buildUtils.js';

const result = dotenv.config({ path: '.env.dev' });
const env = result.parsed || {};

const { defineEnv } = createDefineEnv(env, 'development');

export default merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  devServer: {
    static: './dist',
    port: 3000,
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
        },
      },
    ],
  },
  plugins: [new webpack.DefinePlugin(defineEnv)],
});
