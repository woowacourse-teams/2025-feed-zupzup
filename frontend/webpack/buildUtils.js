import { execSync } from 'child_process';

export const getBuildInfo = (env, envMode = 'production') => {
  try {
    return {
      BUILD_TIME: new Date().toISOString(),
      BUILD_HASH: execSync('git rev-parse --short HEAD').toString().trim(),
      BUILD_HASH_FULL: execSync('git rev-parse HEAD').toString().trim(),
      BRANCH: execSync('git rev-parse --abbrev-ref HEAD').toString().trim(),
      VERSION: env.npm_package_version || '1.0.0',
      COMMIT_MESSAGE: execSync('git log -1 --pretty=%B').toString().trim(),
      ENV_MODE: envMode,
    };
  } catch {
    const fallbackVersion = envMode === 'development' ? '1.0.0-dev' : '1.0.0';
    const fallbackHash =
      envMode === 'development'
        ? 'dev-' + Date.now().toString(36)
        : 'local-' + Date.now().toString(36);

    return {
      BUILD_TIME: new Date().toISOString(),
      BUILD_HASH: fallbackHash,
      BUILD_HASH_FULL: 'unknown',
      BRANCH: 'unknown',
      VERSION: fallbackVersion,
      COMMIT_MESSAGE: `${envMode} build`,
      ENV_MODE: envMode,
    };
  }
};

export const createDefineEnv = (env, envMode) => {
  const buildInfo = getBuildInfo(envMode);

  return {
    defineEnv: {
      'process.env': JSON.stringify({
        ...env,
        ...buildInfo,
      }),
    },
    buildInfo,
  };
};
