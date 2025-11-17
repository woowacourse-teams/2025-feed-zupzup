### 👻 폴더 구조

- 도메인 중심 폴더 구조
- `src/component` : 공통 컴포넌트(도메인 색 X)
  - common 폴더는 `@` prefix로 상단 고정
- `src/**domain**/component`: 도메인 색 반영 + 재사용 컴포넌트
- 타입(type):
  - component 관련 type은 우선 한 페이지 내부에 작성
  - 재사용 필요 시 분리
- 훅(hook):
  - `src/hook` : 프로젝트 전역 공용 훅
  - `src/**domain**/hook` : 필요 시 작성

### 👻 export 선언 방식

- 함수 선언부 앞에 export 붙이기

### 👻 함수 선언 방식

- 컴포넌트/외부 공개 함수: `function`
- 유틸 함수: `arrow function`

### 👻 시맨틱 태그 작성

- header, main, footer, section 우선 사용

### 👻 CSS emotion 변수명

- `-style` suffix 금지

### 👻 import 호출 방식

- `@` 로 시작하는 절대경로 사용 (`src/` 기준)

### 👻 디자인 스타일

- 여백은 2px 단위
- 폰트 크기는 자유롭게 조절 가능

### 👻 interface 명칭

- 컴포넌트/함수 Props → `~Props`
- Route Params → `~Params`
