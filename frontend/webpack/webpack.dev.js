import dotenv from 'dotenv';
import webpack from 'webpack';
import { merge } from 'webpack-merge';
import common from './webpack.common.js';

const result = dotenv.config({ path: '.env.dev' });
const env = result.parsed || {};
const defineEnv = {
  'process.env': JSON.stringify(env),
};

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
  },
  plugins: [new webpack.DefinePlugin(defineEnv)],
});
