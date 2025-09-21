import { useNavigate } from 'react-router-dom';

export default function useNavigation() {
  const navigate = useNavigate();

  const goBack = () => {
    navigate(-1);
  };

  const goPath = (path: string) => {
    navigate(path);
  };

  return {
    goBack,
    goPath,
  };
}
