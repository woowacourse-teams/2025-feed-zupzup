export interface ApiResponse<T = null> {
  data: T;
  status?: number;
  message?: string;
}
