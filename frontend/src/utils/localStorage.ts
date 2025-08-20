export function getLocalStorage<T = string>(key: string): T | null {
  if (typeof window === 'undefined') {
    return null;
  }

  try {
    const data = localStorage.getItem(key);
    if (!data) return [] as T;

    return JSON.parse(data) as T;
  } catch {
    console.error(`${key}값이 존재하지 않습니다.`);
    return null;
  }
}

export function setLocalStorage<T = string>(key: string, dataList: T) {
  localStorage.setItem(key, JSON.stringify(dataList));
}

export function resetLocalStorage(key: string) {
  localStorage.removeItem(key);
}
