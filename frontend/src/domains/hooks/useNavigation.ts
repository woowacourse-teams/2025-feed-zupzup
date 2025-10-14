import { useNavigate } from 'react-router-dom';

export default function useNavigation() {
  const navigate = useNavigate();

  const goBack = () => {
    navigate(-1);
  };

  const goPath = <T = unknown>(path: string, state?: T) => {
    navigate(path, { state });
  };

  return {
    goBack,
    goPath,
  };
}
