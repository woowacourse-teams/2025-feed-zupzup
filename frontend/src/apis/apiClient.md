# API Client 사용법 (fetch 기반 wrapper)

`apiClient`는 fetch를 기반으로 GET, POST, DELETE, PATCH 요청을 간단하게 처리할
수 있도록 래핑한 클라이언트입니다. `onSuccess`, `onError` 콜백을 통해 후처리를
설정할 수 있으며, `ApiError` 클래스를 통해 HTTP 에러 정보를 상세히 확인할 수
있습니다.

---

## ✅ 사용 방법

### 📘 `GET` 요청 예시

```ts
import { apiClient, ApiError } from '@/lib/apiClient';

async function fetchTodo() {
  try {
    const todo = await apiClient.get<{ id: number; title: string }>(
      'https://jsonplaceholder.typicode.com/todos/1'
    );
    console.log(todo);
  } catch (error) {
    if (error instanceof ApiError) {
      console.error('GET 요청 실패:', error.message);
    } else {
      console.error('예기치 못한 에러:', error);
    }
  }
}
```

---

### 📝 `POST` 요청 예시

```ts
import { apiClient, ApiError } from '@/lib/apiClient';

async function createPost() {
  try {
    const newPost = await apiClient.post<
      { id: number; title: string },
      { title: string; body: string; userId: number }
    >('https://jsonplaceholder.typicode.com/posts', {
      title: 'Hello',
      body: 'This is content',
      userId: 1,
    });

    console.log('생성 결과:', newPost);
  } catch (error) {
    if (error instanceof ApiError) {
      console.error('POST 요청 실패:', error.message);
    }
  }
}
```

---

### ❌ `DELETE` 요청 예시

```ts
import { apiClient, ApiError } from '@/lib/apiClient';

async function deletePost() {
  try {
    await apiClient.delete<{}, {}>(
      'https://jsonplaceholder.typicode.com/posts/1'
    );
    console.log('삭제 성공');
  } catch (error) {
    if (error instanceof ApiError) {
      console.error('DELETE 요청 실패:', error.message);
    }
  }
}
```

---

### ✏️ `PATCH` 요청 예시

```ts
import { apiClient, ApiError } from '@/lib/apiClient';

async function updatePost() {
  try {
    const updated = await apiClient.patch<
      { id: number; title: string },
      { title: string }
    >('https://jsonplaceholder.typicode.com/posts/1', {
      title: 'Updated Title',
    });

    console.log('수정 성공:', updated);
  } catch (error) {
    if (error instanceof ApiError) {
      console.error('PATCH 요청 실패:', error.message);
    }
  }
}
```

---

## ⚠️ 예외 처리 방법

- 모든 HTTP 상태 에러는 `ApiError` 인스턴스로 throw 됩니다.
- `try-catch` 내부에서 `instanceof ApiError`로 확인하여 상태 코드 기반 분기
  처리가 가능합니다.

```ts
try {
  await apiClient.get('/some/url');
} catch (error) {
  if (error instanceof ApiError) {
    if (error.status === 401) {
      // 인증 에러 처리
    } else if (error.status === 500) {
      // 서버 에러 처리
    }
  } else {
    // 네트워크 오류 또는 예기치 않은 에러
  }
}
```

---

## ✅ 참고

- 기본 timeout: 5초 (5000ms)
- `isEmptyResponse`, `isSuccess`, `isErrorWithStatus` 유틸 함수로 HTTP 응답 분기
  처리
- `ApiError`는 `status`, `message` 속성을 포함

```ts
class ApiError extends Error {
  constructor(
    public status: number,
    public message: string
  ) {
    super(message);
    this.name = 'ApiError';
  }
}
```
