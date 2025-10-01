import CopyWebpackPlugin from 'copy-webpack-plugin';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import path, { dirname } from 'path';
import { fileURLToPath } from 'url';
import { BundleAnalyzerPlugin } from 'webpack-bundle-analyzer';
import TerserPlugin from 'terser-webpack-plugin';

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
  output: {
    filename: '[name].[contenthash].js',
    chunkFilename: '[name].[contenthash].js',
    assetModuleFilename: 'assets/[name].[contenthash][ext]',
    path: path.resolve(__dirname, '../dist'),
    clean: true,
    publicPath: '/',
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
              '@babel/preset-typescript',
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
        test: /\.(png|svg|jpg|jpeg|gif|webp)$/i,
        type: 'asset/resource',
        generator: {
          filename: 'images/[name].[hash][ext]',
        },
      },
      {
        test: /\.(woff|woff2)$/i,
        type: 'asset/resource',
        generator: {
          filename: 'assets/fonts/[name].[hash][ext]',
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
        { from: 'src/assets/fonts', to: 'assets/fonts' },
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
  optimization: {
    minimize: true,
    splitChunks: {
      chunks: 'all',
      cacheGroups: {
        react: {
          test: /[\\/]node_modules[\\/](react|react-dom)[\\/]/,
          name: 'react',
          chunks: 'all',
          priority: 40,
        },
        router: {
          test: /[\\/]node_modules[\\/]react-router(.*)[\\/]/,
          name: 'router',
          chunks: 'all',
          priority: 30,
        },
        query: {
          test: /[\\/]node_modules[\\/]@tanstack[\\/]query(.*)[\\/]/,
          name: 'query',
          chunks: 'all',
          priority: 30,
        },
        motion: {
          test: /[\\/]node_modules[\\/]framer-motion[\\/]/,
          name: 'motion',
          chunks: 'all',
          priority: 20,
        },
        motionDom: {
          test: /[\\/]node_modules[\\/]motion-dom[\\/]/,
          name: 'motionDom',
          chunks: 'all',
          priority: 20,
        },
        sentry: {
          test: /[\\/]node_modules[\\/]@sentry[\\/]/,
          name: 'sentry',
          chunks: 'all',
          priority: 20,
        },
        sentryInternal: {
          test: /[\\/]node_modules[\\/]@sentry-internal[\\/]/,
          name: 'sentryInternal',
          chunks: 'all',
          priority: 20,
        },
        firebase: {
          test: /[\\/]node_modules[\\/]@firebase[\\/]/,
          name: 'firebase',
          chunks: 'all',
          priority: 20,
        },
        vendors: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendors',
          chunks: 'all',
          priority: 10,
        },
      },
    },
    minimizer: [
      new TerserPlugin({
        terserOptions: {
          compress: {
            // drop_console: true,
            drop_debugger: true,
          },
          mangle: true,
          output: {
            comments: false,
          },
        },
        extractComments: false,
      }),
    ],
  },
};
