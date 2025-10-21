import dotenv from 'dotenv';
import webpack from 'webpack';
import { merge } from 'webpack-merge';
import { InjectManifest } from 'workbox-webpack-plugin';
import path, { dirname } from 'path';
import { fileURLToPath } from 'url';
import { createDefineEnv } from './buildUtils.js';
import common from './webpack.common.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const result = dotenv.config({ path: '.env.dev' });
const env = result.parsed || {};

const defineEnv = createDefineEnv(env, 'development');

export default merge(common, {
  mode: 'production',
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
  },
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
        /node_modules/,
        /\.(woff|woff2)$/,
        /screenshot.*\.png$/,
      ],
      maximumFileSizeToCacheInBytes: 3 * 1024 * 1024,
      mode: 'development',
    }),
  ],
});
