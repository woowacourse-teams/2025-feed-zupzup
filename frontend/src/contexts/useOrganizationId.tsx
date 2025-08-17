import {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from 'react';
import { useParams } from 'react-router-dom';

interface OrganizationIdContextType {
  organizationId: string;
  setOrganizationId: (id: string) => void;
}

const OrganizationIdContext = createContext<
  OrganizationIdContextType | undefined
>(undefined);

interface OrganizationIdProviderProps {
  children: ReactNode;
}

export function OrganizationIdProvider({
  children,
}: OrganizationIdProviderProps) {
  const [organizationId, setOrganizationId] = useState<string>('');
  const params = useParams();

  useEffect(() => {
    if (params.id) {
      setOrganizationId(params.id);
    }
  }, [params.id]);

  return (
    <OrganizationIdContext.Provider
      value={{ organizationId, setOrganizationId }}
    >
      {children}
    </OrganizationIdContext.Provider>
  );
}

export function useOrganizationId() {
  const context = useContext(OrganizationIdContext);
  if (context === undefined) {
    throw new Error(
      'useOrganizationId는 OrganizationIdProvider 내에서 사용되어야 합니다.'
    );
  }
  return context;
}
