import dotenv from 'dotenv';
import webpack from 'webpack';
import { merge } from 'webpack-merge';
import { createDefineEnv } from './buildUtils.js';
import common from './webpack.common.js';

const result = dotenv.config({ path: '.env.prod' });
const env = result.parsed || {};

const defineEnv = createDefineEnv(env, 'production');

export default merge(common, {
  mode: 'production',
  devtool: 'source-map',
  plugins: [new webpack.DefinePlugin(defineEnv)],
});
