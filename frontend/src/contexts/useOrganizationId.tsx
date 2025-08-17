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
  isLoading: boolean; // 로딩 상태 추가
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
  const [isLoading, setIsLoading] = useState(true); // 초기값을 true로 설정
  const params = useParams();

  useEffect(() => {
    if (params.id) {
      setOrganizationId(params.id);
    }
    setIsLoading(false); // params 확인 후 로딩 완료
  }, [params.id]);

  return (
    <OrganizationIdContext.Provider
      value={{ organizationId, setOrganizationId, isLoading }}
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
