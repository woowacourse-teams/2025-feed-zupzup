import { useState } from 'react';
import { apiClient } from '@/apis/apiClient';

const DEFAULT_SIZE = 10;

interface UseInfinityScrollProps<ResponseData> {
  url: string;
  key: keyof ResponseData;
  initialCursorId?: number | null;
  initialHasNext?: boolean;
}

export default function useInfinityScroll<
  T extends object,
  Key extends string,
  ResponseData extends Record<Key, T[]> & {
    hasNext: boolean;
    nextCursorId: number;
  },
>({
  url,
  key,
  initialCursorId = null,
  initialHasNext = true,
}: UseInfinityScrollProps<ResponseData>) {
  const [items, setItems] = useState<T[]>([]);
  const [cursorId, setCursorId] = useState<number | null>(initialCursorId);
  const [hasNext, setHasNext] = useState(initialHasNext);
  const [loading, setLoading] = useState(false);

  const fetchMore = async (size: number = DEFAULT_SIZE) => {
    if (!hasNext || loading) return;
    setLoading(true);

    const query = new URLSearchParams({
      size: size.toString(),
      ...(cursorId !== null && { cursorId: cursorId.toString() }),
    });

    try {
      const response = await apiClient.get<{ data: ResponseData }>(
        `${process.env.BASE_URL}${url}?${query.toString()}`
      );

      if (!response) return;

      const responseData = response.data;

      setItems((prev) => [...prev, ...responseData[key]]);
      setHasNext(responseData.hasNext);
      setCursorId(responseData.nextCursorId);
    } catch (error) {
      console.error('무한 스크롤 에러:', error);
    } finally {
      setLoading(false);
    }
  };

  return { items, fetchMore, hasNext, loading };
}
