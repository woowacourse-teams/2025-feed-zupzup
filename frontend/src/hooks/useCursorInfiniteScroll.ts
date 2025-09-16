import { apiClient, ApiError } from '@/apis/apiClient';
import { ApiResponse } from '@/types/apiResponse';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useApiErrorHandler } from './useApiErrorHandler';

const DEFAULT_SIZE = 10;
const MAX_RETRY_COUNT = 1;

interface UseCursorInfiniteScrollParams<Key extends string> {
  url: string; // 예: /api/items?foo=bar
  key: Key; // 응답에서 아이템 배열의 키
  size?: number; // 페이지 사이즈 (기본 10)
  enabled?: boolean; // false면 패칭 비활성화
}

type CursorResponse<T, Key extends string> = Record<Key, T[]> & {
  hasNext: boolean;
  nextCursorId: number;
};

export default function useCursorInfiniteScroll<
  T extends object,
  Key extends string,
  ResponseData extends CursorResponse<T, Key>,
>({
  url,
  key,
  size = DEFAULT_SIZE,
  enabled = true,
}: UseCursorInfiniteScrollParams<Key>) {
  const { handleApiError } = useApiErrorHandler();

  const query = useInfiniteQuery({
    queryKey: ['infinity', key, url, size],
    enabled: enabled && Boolean(url),
    retry: MAX_RETRY_COUNT,
    initialPageParam: null as number | null,
    queryFn: ({ pageParam }) =>
      fetchCursorPage<ResponseData>({
        url,
        size,
        pageParam,
      }),
    getNextPageParam: (lastPage) =>
      lastPage?.hasNext ? lastPage?.nextCursorId : undefined,
  });

  const items = query.data?.pages.flatMap((p) => p[key] as T[]) ?? [];

  if (query.isError) {
    handleApiError(query.error as ApiError);
  }

  return {
    items,
    fetchMore: query.fetchNextPage,
    hasNext: query.hasNextPage,
    loading: query.isFetchingNextPage,
  };
}

interface fetchCursorPageParams {
  url: string;
  size: number;
  pageParam: number | null;
}

export async function fetchCursorPage<ResponseData>(
  params: fetchCursorPageParams
): Promise<ResponseData> {
  const { url, size, pageParam } = params;

  const queryString = new URLSearchParams({ size: String(size) });
  if (pageParam != null) queryString.set('cursorId', String(pageParam));

  const sep = url.includes('?') ? '&' : '?';
  const requestUrl = `${url}${sep}${queryString.toString()}`;

  const res = await apiClient.get<ApiResponse<ResponseData>>(requestUrl);

  const payload = res?.data as ResponseData;
  return payload;
}
