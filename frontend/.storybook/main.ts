import type { StorybookConfig } from '@storybook/react-webpack5';
import path from 'path';

const config: StorybookConfig = {
  stories: ['../src/**/*.mdx', '../src/**/*.stories.@(js|jsx|mjs|ts|tsx)'],
  addons: [
    '@storybook/addon-webpack5-compiler-swc',
    '@storybook/addon-docs',
    '@storybook/addon-onboarding',
    '@storybook/addon-themes',
  ],
  framework: {
    name: '@storybook/react-webpack5',
    options: {},
  },
  webpackFinal: async (config) => {
    if (config.resolve) {
      config.resolve.alias = {
        ...config.resolve.alias,
        '@': path.resolve(__dirname, '../src'),
      };
    }

    if (config.module?.rules) {
      const jsRule = config.module.rules.find((rule) => {
        if (typeof rule === 'object' && rule && 'test' in rule) {
          return rule.test?.toString().includes('jsx?');
        }
        return false;
      });

      if (jsRule && typeof jsRule === 'object' && jsRule && 'use' in jsRule) {
        const babelLoader = Array.isArray(jsRule.use)
          ? jsRule.use.find(
              (loader) =>
                typeof loader === 'object' &&
                loader &&
                'loader' in loader &&
                loader.loader?.includes('babel-loader')
            )
          : typeof jsRule.use === 'object' &&
              jsRule.use &&
              'loader' in jsRule.use &&
              jsRule.use.loader?.includes('babel-loader')
            ? jsRule.use
            : null;

        if (
          babelLoader &&
          typeof babelLoader === 'object' &&
          'options' in babelLoader
        ) {
          babelLoader.options = {
            ...(babelLoader.options as Record<string, unknown>),
            presets: [
              ...(typeof babelLoader.options === 'object' &&
              babelLoader.options &&
              'presets' in babelLoader.options &&
              Array.isArray(babelLoader.options.presets)
                ? babelLoader.options.presets
                : []),
              '@emotion/babel-preset-css-prop',
            ],
          };
        }
      }

      config.module.rules.push({
        test: /\.(ts|tsx)$/,
        use: [
          {
            loader: require.resolve('babel-loader'),
            options: {
              presets: [
                [
                  '@babel/preset-react',
                  { runtime: 'automatic', importSource: '@emotion/react' },
                ],
                '@babel/preset-typescript',
                '@emotion/babel-preset-css-prop',
              ],
              plugins: ['@emotion/babel-plugin'],
            },
          },
        ],
      });
    }

    return config;
  },
};

export default config;
