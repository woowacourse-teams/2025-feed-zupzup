import { apiClient } from '@/apis/apiClient';

interface PostAdminLoginParams {
  id: string;
  password: string;
  onError: () => void;
  onSuccess: () => void;
}

export async function postAdminLogin({
  id,
  password,
  onError,
  onSuccess,
}: PostAdminLoginParams) {
  const response = await apiClient.post(
    '/admin/login',
    {
      id,
      password,
    },
    {
      onError,
      onSuccess,
    }
  );
  return response;
}
