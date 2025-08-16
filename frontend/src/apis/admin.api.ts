import { apiClient } from '@/apis/apiClient';

interface PostAdminLoginParams {
  loginId: string;
  password: string;
  onError: () => void;
  onSuccess: () => void;
}

interface AdminLoginResponse {
  data: {
    loginId: string;
    adminName: string;
    adminId: number;
  };
  status: number;
  message: string;
}

export async function postAdminLogin({
  loginId,
  password,
  onError,
  onSuccess,
}: PostAdminLoginParams): Promise<AdminLoginResponse> {
  const response = await apiClient.post(
    '/admin/login',
    {
      loginId,
      password,
    },
    {
      onError,
      onSuccess,
    }
  );
  return response as AdminLoginResponse;
}
