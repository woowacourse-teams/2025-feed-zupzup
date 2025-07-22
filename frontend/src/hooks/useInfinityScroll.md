# 📌 useInfinityScroll 훅 사용법

`useInfinityScroll`은 커서 기반 API 응답을 처리하여 무한 스크롤 기능을 구현할 수
있도록 도와주는 커스텀 훅입니다.  
공통 응답 구조를 기반으로 어떤 API든 재사용 가능하도록 제네릭 타입과 key
기반으로 유연하게 설계되어 있습니다.

---

## ✅ 사용 예시

```tsx
import useInfinityScroll from '@/hooks/useInfinityScroll';

interface Feedback {
  id: number;
  content: string;
  createdAt: string;
}

interface FeedbackResponse {
  feedbacks: Feedback[];
  hasNext: boolean;
  cursorId: number;
}

const {
  items: feedbacks,
  fetchMore,
  hasNext,
  loading,
} = useInfinityScroll<Feedback, 'feedbacks', FeedbackResponse>({
  url: '/api/places/1/feedbacks',
  key: 'feedbacks',
});
```

---

# ⚙️ 제네릭 타입 설명

훅은 총 세 개의 제네릭 타입 매개변수를 받습니다.

`seInfinityScroll<T, Key, ResponseData>()`

### 타입 매개변수 의미

- T
  - 아이템 하나의 타입 (예: Feedback, Comment 등)
- Key
  - 응답 객체에서 배열 데이터를 담고 있는 key의 문자열 리터럴 (예: 'feedbacks',
    'comments')
- ResponseData
  - 서버의 전체 응답 데이터 타입 (아래 구조를 포함해야 함)

```ts
type ResponseData = {
  [key in Key]: T[];
} & {
  hasNext: boolean;
  cursorId: number;
};
```

---

# 🔑 key 파라미터는 왜 필요한가요?

API의 응답 데이터가 다음과 같다고 가정해봅시다.

```ts
{
  "data": {
    "feedbacks": [ /* ... */ ],
    "hasNext": true,
    "cursorId": 3
  }
}
```

- 이때 feedbacks, comments 등 리스트 데이터의 key는 API마다 다를 수 있습니다.
- 훅 내부에서는 response.data[key]로 접근해야 하므로, 외부에서 key를 문자열
  리터럴로 명시해줘야 타입 안정성이 확보됩니다.

---

# 📦 훅에서 반환되는 값

### 반환값 타입 설명

- items
  - `T[]` 현재까지 불러온 아이템 목록
- fetchMore()
  - `() => Promise<void>` 다음 데이터를 요청하는 함수
- hasNext
  - `boolean` 더 불러올 데이터가 있는지 여부
- loading
  - `boolean` 현재 로딩 중인지 여부

---

# 🧪 스크롤 이벤트로 fetchMore 호출하기

```ts
const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
  const { scrollTop, clientHeight, scrollHeight } = e.currentTarget;
  if (scrollTop + clientHeight >= scrollHeight - 10 && hasNext && !loading) {
    fetchMore();
  }
};
```

⸻

# 🔐 전제 조건

- apiClient.get 호출은 다음과 같은 응답 구조를 반환해야 합니다:

```ts
{
  data: {
    [key: string]: T[];
    hasNext: boolean;
    cursorId: number;
  };
}
```

- URL은 쿼리 파라미터를 포함하지 않은 기본 경로여야 하며, 훅 내부에서
  ?size=X&cursorId=Y 형태로 조합됩니다.
