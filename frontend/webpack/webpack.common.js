import CopyWebpackPlugin from 'copy-webpack-plugin';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import path, { dirname } from 'path';
import { fileURLToPath } from 'url';
import { BundleAnalyzerPlugin } from 'webpack-bundle-analyzer';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const isAnalyze = process.env.ANALYZE === 'true';

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
        'Cache-Control': 'no-cache, max-age=0',
      },
    }),
    new CopyWebpackPlugin({
      patterns: [
        { from: 'public/favicon.ico', to: '.' },
        { from: 'public/manifest.json', to: '.' },
        { from: 'public/512x512.png', to: '.' },
        { from: 'public/192x192.png', to: '.' },
        { from: 'public/service-worker.js', to: '.' },
        {
          from: 'public',
          to: '.',
          globOptions: {
            ignore: ['**/index.html'],
          },
        },
      ],
    }),
    ...(isAnalyze
      ? [
          new BundleAnalyzerPlugin({
            analyzerMode: 'server',
            openAnalyzer: true,
            reportFilename: 'bundle-report.html',
          }),
        ]
      : []),
  ],
};
