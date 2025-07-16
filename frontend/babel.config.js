export default {
  presets: [
    ['@babel/preset-react', { runtime: 'automatic' }],
    '@babel/preset-env',
    '@babel/preset-typescript',
  ],
  plugins: [
    [
      '@emotion/babel-plugin',
      { sourceMap: true, autoLabel: 'dev-only', labelFormat: '[local]' },
    ],
  ],
};
