# debounce 함수

`debounce`는 빠르게 반복되는 함수 호출을 제어하기 위해 사용하는 유틸리티
함수입니다.  
지정한 `delay` 시간 동안 추가 호출이 없을 때에만 `callback`이 실행되도록 합니다.

이 함수는 **제네릭 타입**을 사용하여 전달된 `callback`의 매개변수 타입을 그대로
추론하므로, 타입 안정성을 유지하면서 사용할 수 있습니다.

---

# ✨ 시그니처

```ts
export function useDebounce<T extends (...args: unknown[]) => void>(
  callback: T,
  delay: number
): (...args: Parameters<T>) => void;
```

- T: 함수 타입 제네릭
- callback: 디바운싱을 적용할 함수
- delay: 밀리초 단위의 지연 시간
- 반환값: 디바운싱이 적용된 새로운 함수

---

# 🔍 동작 원리

- 마지막 호출 시점으로부터 delay만큼 대기한 후 callback을 실행합니다.
- 만약 delay 이내에 다시 호출되면 이전 타이머를 취소하고 새로 설정합니다.
- 사용자는 디바운싱이 적용된 함수를 호출만 하면 됩니다.

---

# ✅ 예시

```ts
// 디바운싱 적용: 입력 후 500ms 동안 입력이 없을 때만 실행
const debouncedSearch = useDebounce((searchTerm: string) => {
  console.log('검색 요청:', searchTerm);
  // fetchData(searchTerm) 등의 API 요청
}, 500);

const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  const value = e.target.value;
  debouncedSearch(value); // 매 입력마다 debounce 함수 호출
};
```

---

# useRef 사용 이유

1. callbackRef

- useCallback 안에서 최신 콜백을 안정적으로 참조하기 위해 사용합니다.
- 컴포넌트가 리렌더링될 때 새로운 callback이 생성되더라도, callbackRef.current를
  갱신해주기 때문에 debounced 함수가 항상 최신 callback을 실행합니다.
- 컴포넌트가 리렌더링되면서 함수가 재생성되는데, 함수의 참조값이 바뀌더라도 훅의
  참조값을 useRef로 저장함으로서 debounce된 함수가 재생성되지 않도록 함

2. timerRef

- 타이머 ID를 저장하여 이전 타이머를 취소할 수 있게 합니다.
- useRef를 쓰지 않으면 훅이 재실행될 때, 함수가 재생성되며 timer도 초기화되기
  때문에 이전 타이머를 추적할 수 없습니다.
- ref는 렌더 사이클과 관계없이 값을 유지할 수 있어 타이머 관리에 적합합니다.
