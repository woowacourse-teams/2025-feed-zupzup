import dotenv from 'dotenv';
import webpack from 'webpack';
import { merge } from 'webpack-merge';
import TerserPlugin from 'terser-webpack-plugin';
import { createDefineEnv } from './buildUtils.js';
import common from './webpack.common.js';
import { InjectManifest } from 'workbox-webpack-plugin';
import path, { dirname } from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const result = dotenv.config({ path: '.env.prod' });
const env = result.parsed || {};

const defineEnv = createDefineEnv(env, 'production');

export default merge(common, {
  mode: 'production',
  devtool: 'source-map',
  plugins: [
    new webpack.DefinePlugin(defineEnv),
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
      mode: 'production',
    }),
  ],
  optimization: {
    minimizer: [
      new TerserPlugin({
        terserOptions: {
          compress: {
            drop_console: true,
            pure_funcs: ['console.log', 'console.info', 'console.debug'],
          },
        },
      }),
    ],
  },
});
