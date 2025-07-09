import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import react from 'eslint-plugin-react';
import reactHooks from 'eslint-plugin-react-hooks';
import prettierPlugin from 'eslint-plugin-prettier';
import prettierConfig from 'eslint-config-prettier';

export default [
    js.configs.recommended,
    ...tseslint.configs.recommended,
    prettierConfig,
    {
        files: ['**/*.ts', '**/*.tsx'],
        plugins: {
            '@typescript-eslint': tseslint.plugin,
            react,
            'react-hooks': reactHooks,
            prettier: prettierPlugin,
        },
        languageOptions: {
            parser: tseslint.parser,
            parserOptions: {
                project: './tsconfig.json',
                ecmaVersion: 'latest',
                sourceType: 'module',
                ecmaFeatures: {
                    jsx: true,
                },
            },
        },
        rules: {
            'react/react-in-jsx-scope': 'off',
            'prettier/prettier': 'error',
            quotes: ['error', 'single', { avoidEscape: true }],
        },
    },
];
