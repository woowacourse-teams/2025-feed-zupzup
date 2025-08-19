import CopyWebpackPlugin from 'copy-webpack-plugin';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import path, { dirname } from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

export default {
  entry: './src/index.tsx',
  resolve: {
    extensions: ['.ts', '.tsx', '.js'],
    alias: {
      '@': path.resolve(__dirname, '../src'),
    },
  },
  devServer: {
    historyApiFallback: true,
    static: './dist',
    port: 3000,
  },
  module: {
    rules: [
      {
        test: /\.(js|ts|tsx)$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: [
              [
                '@babel/preset-react',
                {
                  runtime: 'automatic',
                  importSource: '@emotion/react',
                },
              ],
            ],
            plugins: ['@emotion/babel-plugin'],
          },
        },
      },
      {
        test: /\.(png|svg|jpg|jpeg|gif)$/i,
        type: 'asset/resource',
        generator: {
          filename: 'images/[name].[hash][ext]',
        },
      },
      {
        test: /.css$/i,
        use: ['style-loader', 'css-loader'],
      },
    ],
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: './public/index.html',
      meta: {
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        Pragma: 'no-cache',
        Expires: '0',
      },
    }),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: 'public/favicon.ico',
          to: '.',
        },
        {
          from: 'public/manifest.json',
          to: '.',
        },
        {
          from: 'public/512x512.png',
          to: 'images/[name].[contenthash][ext]',
        },
        {
          from: 'public/192x192.png',
          to: 'images/[name].[contenthash][ext]',
        },
        {
          from: 'public/service-worker.js',
          to: 'sw.[contenthash].js',
          transform(content) {
            const versionComment = `// SW Version: ${Date.now()}\n`;
            return versionComment + content;
          },
        },
        {
          from: 'public',
          to: '.',
          globOptions: {
            ignore: [
              '**/index.html',
              '**/favicon.ico',
              '**/manifest.json',
              '**/512x512.png',
              '**/192x192.png',
              '**/service-worker.js',
            ],
          },
        },
      ],
    }),
  ],
};
