import { useRef, useState } from 'react';
import { apiClient } from '@/apis/apiClient';

const DEFAULT_SIZE = 10;
const MAX_RETRY_COUNT = 3;

interface UseInfinityScrollParams<ResponseData> {
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
}: UseInfinityScrollParams<ResponseData>) {
  const [items, setItems] = useState<T[]>([]);
  const [cursorId, setCursorId] = useState<number | null>(initialCursorId);
  const [hasNext, setHasNext] = useState(initialHasNext);
  const [loading, setLoading] = useState(false);

  const retryCountRef = useRef(0);

  const fetchMore = async (size: number = DEFAULT_SIZE) => {
    if (!hasNext || loading) return;
    setLoading(true);

    const query = new URLSearchParams({
      size: size.toString(),
      ...(cursorId !== null && { cursorId: cursorId.toString() }),
    });

    try {
      const response = await apiClient.get<{ data: ResponseData }>(
        `${url}?${query.toString()}`
      );

      if (!response) throw new Error('응답 없음');

      const responseData = response.data;

      setItems((prev) => [...prev, ...responseData[key]]);
      setHasNext(responseData.hasNext);
      setCursorId(responseData.nextCursorId);
      retryCountRef.current = 0;
    } catch (error) {
      setHasNext(false);
      console.error('무한 스크롤 에러:', error);

      retryCountRef.current += 1;
      if (retryCountRef.current >= MAX_RETRY_COUNT) {
        setHasNext(false);
      }
    } finally {
      setLoading(false);
    }
  };

  return { items, fetchMore, hasNext, loading };
}
