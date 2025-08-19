export const isAuthenticated = () => {
  try {
    const auth = localStorage.getItem('auth');
    if (!auth) return false;

    const parsedAuth = JSON.parse(auth);
    return Boolean(
      parsedAuth?.adminId && parsedAuth?.adminName && parsedAuth?.loginId
    );
  } catch {
    localStorage.removeItem('auth');
    return false;
  }
};
