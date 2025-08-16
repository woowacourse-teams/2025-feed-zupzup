import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/apiResponse';

interface PostAdminLoginParams {
  loginId: string;
  password: string;
  onError: () => void;
  onSuccess: (data: Response) => void;
}

interface PostAdminSignupParams {
  loginId: string;
  password: string;
  adminName: string;
  onError: () => void;
  onSuccess: (data: Response) => void;
}

interface GetAdminAuthProps {
  onError: () => void;
  onSuccess: (data: AdminAuthData) => void;
}

export type AdminAuthData = {
  loginId: string;
  adminName: string;
  adminId: number;
};

type PostAdminLogoutResponse = ApiResponse<string>;

export async function postAdminLogin({
  loginId,
  password,
  onError,
  onSuccess,
}: PostAdminLoginParams) {
  await apiClient.post(
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
}

export async function postAdminLogout(): Promise<PostAdminLogoutResponse> {
  const response = await apiClient.post('/admin/logout', {});

  return response as PostAdminLogoutResponse;
}

export async function postAdminSignup({
  loginId,
  password,
  adminName,
  onError,
  onSuccess,
}: PostAdminSignupParams) {
  await apiClient.post(
    '/admin/sign-up',
    {
      loginId,
      password,
      adminName,
    },
    {
      onError,
      onSuccess,
    }
  );
}

export async function getAdminAuth({ onError, onSuccess }: GetAdminAuthProps) {
  await apiClient.get('/admin/me', {
    onError,
    onSuccess,
  });
}
