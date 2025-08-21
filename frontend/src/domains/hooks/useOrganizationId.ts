import { useParams } from 'react-router-dom';

export function useOrganizationId() {
  const { id: organizationId } = useParams();
  return { organizationId: organizationId || '' };
}
