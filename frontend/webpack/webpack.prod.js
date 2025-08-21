import { merge } from 'webpack-merge';
import common from './webpack.common.js';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import dotenv from 'dotenv';
import webpack from 'webpack';
import { createDefineEnv } from './buildUtils.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const result = dotenv.config({ path: '.env.prod' });
const env = result.parsed || {};

const defineEnv = createDefineEnv(env, 'production');

export default merge(common, {
  mode: 'production',
  output: {
    filename: '[name].[contenthash].js',
    chunkFilename: '[name].[contenthash].js',
    assetModuleFilename: 'assets/[name].[contenthash][ext]',
    path: path.resolve(__dirname, '../dist'),
    clean: true,
    publicPath: '/',
  },
  devtool: 'source-map',
  plugins: [new webpack.DefinePlugin(defineEnv)],
});
