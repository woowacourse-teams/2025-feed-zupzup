import { apiClient } from '@/apis/apiClient';
import { ApiResponse } from '@/types/apiResponse';

interface PostAdminLoginParams {
  loginId: string;
  password: string;
  onSuccess: (response: AdminAuthResponse) => void;
}

interface PostAdminSignupParams {
  loginId: string;
  password: string;
  adminName: string;
  onSuccess: (response: AdminAuthResponse) => void;
}

interface GetAdminAuthProps {
  onSuccess: (response: AdminAuthResponse) => void;
}

export type AdminAuthData = {
  loginId: string;
  adminName: string;
  adminId: number;
};

type PostAdminLogoutResponse = ApiResponse<string>;
export type AdminAuthResponse = ApiResponse<AdminAuthData>;

export async function postAdminLogin({
  loginId,
  password,
  onSuccess,
}: PostAdminLoginParams) {
  await apiClient.post(
    '/admin/login',
    {
      loginId,
      password,
    },
    {
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
      onSuccess,
    }
  );
}

export async function getAdminAuth({ onSuccess }: GetAdminAuthProps) {
  await apiClient.get('/admin/me', {
    onSuccess,
  });
}
