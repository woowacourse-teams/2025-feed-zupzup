interface VersionInfo {
  version: string;
  buildHash: string;
  buildHashFull: string;
  branch: string;
  buildTime: string;
  buildTimeIso: string;
  commitMessage: string;
  envMode: string;
}

export const versionInfo: VersionInfo = {
  version: process.env.VERSION || 'unknown',
  buildHash: process.env.BUILD_HASH || 'unknown',
  buildHashFull: process.env.BUILD_HASH_FULL || 'unknown',
  branch: process.env.BRANCH || 'unknown',
  buildTime: process.env.BUILD_TIME || 'unknown',
  buildTimeIso: process.env.BUILD_TIME_ISO || 'unknown',
  commitMessage: process.env.COMMIT_MESSAGE || 'unknown',
  envMode: process.env.ENV_MODE || 'unknown',
};

const formatToKST = (isoTime: string): string => {
  try {
    const date = new Date(isoTime);
    return date.toLocaleString('ko-KR', {
      timeZone: 'Asia/Seoul',
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    });
  } catch {
    return isoTime;
  }
};

export const logBuildInfo = () => {
  const envIcon = versionInfo.envMode === 'development' ? '🛠️' : '🚀';
  const envColor =
    versionInfo.envMode === 'development' ? '#FF9800' : '#4CAF50';

  console.group(
    `${envIcon} Feed-ZupZup Build Information (${versionInfo.envMode?.toUpperCase()})`
  );
  console.log(
    `%c🏷️ Version: ${versionInfo.version}`,
    `color: ${envColor}; font-weight: bold;`
  );
  console.log(
    `%c📦 Build Hash: ${versionInfo.buildHash}`,
    'color: #2196F3; font-weight: bold;'
  );
  console.log(
    `%c🌿 Branch: ${versionInfo.branch}`,
    'color: #FF9800; font-weight: bold;'
  );
  console.log(
    `%c⏰ Build Time: ${formatToKST(versionInfo.buildTime)} (KST)`,
    'color: #9C27B0; font-weight: bold;'
  );
  console.log(
    `%c💬 Last Commit: ${versionInfo.commitMessage}`,
    'color: #607D8B;'
  );
  console.groupEnd();
};

export const setupGlobalVersion = () => {
  const versionWithKST = {
    ...versionInfo,
    buildTimeKST: formatToKST(versionInfo.buildTime),
  };

  window.__APP_VERSION__ = versionWithKST;
  window.getVersion = () => {
    console.table(versionWithKST);
    return versionWithKST;
  };
  logBuildInfo();
};
