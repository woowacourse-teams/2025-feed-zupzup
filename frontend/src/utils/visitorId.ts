const VISITOR_ID = 'visitorId';

function getVisitorIdFromCookie(): string | null {
  const cookies = document.cookie.split(';');
  const visitorCookie = cookies.find((cookie) =>
    cookie.trim().startsWith(`${VISITOR_ID}=`)
  );

  if (!visitorCookie) return null;

  return visitorCookie.split('=')[1].trim();
}

function setVisitorIdToCookie(id: string): void {
  const maxAge = 60 * 60 * 24 * 365 * 10;

  document.cookie = `${VISITOR_ID}=${id};max-age=${maxAge};path=/;SameSite=Lax`;
}

function getVisitorIdFromLocalStorage(): string | null {
  return localStorage.getItem(VISITOR_ID);
}

function setVisitorIdToLocalStorage(id: string): void {
  localStorage.setItem(VISITOR_ID, id);
}

export function syncVisitorId(): string | null {
  let visitorId = getVisitorIdFromCookie();
  console.log('visitorId', visitorId);

  if (visitorId) {
    setVisitorIdToLocalStorage(visitorId);
    return visitorId;
  }

  visitorId = getVisitorIdFromLocalStorage();

  if (visitorId) {
    setVisitorIdToCookie(visitorId);
    return visitorId;
  }
  return null;
}

export function getVisitorId(): string | null {
  return syncVisitorId();
}

export function saveVisitorIdFromBackend(): void {
  const visitorId = getVisitorIdFromCookie();

  if (visitorId) {
    setVisitorIdToLocalStorage(visitorId);
  }
}
