import { NavigateOptions, useNavigate } from 'react-router-dom';

export default function useNavigation() {
  const navigate = useNavigate();

  const goBack = () => {
    navigate(-1);
  };

  const goPath = (path: string, options?: NavigateOptions) => {
    navigate(path, options);
  };

  return {
    goBack,
    goPath,
  };
}
