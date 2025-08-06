import { merge } from 'webpack-merge';
import common from './webpack.common.js';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import dotenv from 'dotenv';
import webpack from 'webpack';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const result = dotenv.config({ path: '.env.prod' });
const env = result.parsed || {};
const defineEnv = {
  'process.env': JSON.stringify(env),
};

export default merge(common, {
  mode: 'production',
  output: {
    filename: 'bundle.[contenthash].js',
    path: path.resolve(__dirname, '../dist'),
    clean: true,
  },
  devtool: 'source-map',
  plugins: [new webpack.DefinePlugin(defineEnv)],
});
