import { ApiError } from '@/apis/apiClient';
import { getOrganizationName } from '@/apis/organization.api';
import { useErrorModalContext } from '@/contexts/useErrorModal';
import { useEffect, useState } from 'react';

export default function useOrganizationName() {
  const [placeName, setPlaceName] = useState('피드줍줍');
  const { setMessage, setTitle, setErrorTrue } = useErrorModalContext();

  useEffect(() => {
    async function getOrganization() {
      try {
        const response = await getOrganizationName({ organizationId: 10 });
        setPlaceName(response!.data.organizationName);
      } catch (e) {
        if (e instanceof ApiError) {
          console.log('dfd');
          setMessage(e.message);
          setTitle('에러 발생');
          setErrorTrue();
        }
      }
    }

    getOrganization();
  }, []);

  return { placeName };
}
