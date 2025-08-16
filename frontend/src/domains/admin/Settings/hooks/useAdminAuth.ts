import { AdminAuthData, getAdminAuth } from '@/apis/admin.api';

import { useEffect, useState } from 'react';

export default function useAdminAuth() {
  const [adminAuth, setAdminAuth] = useState<AdminAuthData | null>(null);

  useEffect(() => {
    (async () => {
      const response = await getAdminAuth();
      if (response) {
        setAdminAuth(response.data);
      } else {
        setAdminAuth(null);
      }
    })();
  }, []);

  return { adminAuth };
}
