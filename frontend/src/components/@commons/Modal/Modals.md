# Modal 컴포넌트 사용법 가이드

## 📁 파일 구조

```
frontend/src/components/
├── @commons/Modal/
│   └── Modal.tsx                 # 공통 Modal 컴포넌트
├── AlertModal/
│   ├── AlertModal.tsx           # Alert 모달 컴포넌트
│   └── AlertModal.styles.ts     # Alert 모달 스타일
└── ConfirmModal/
    ├── ConfirmModal.tsx         # Confirm 모달 컴포넌트
    └── ConfirmModal.styles.ts   # Confirm 모달 스타일

frontend/src/hooks/
└── useModal.ts                  # 모달 공통 로직 훅
```

## 🎯 컴포넌트별 역할

### 1. Modal (공통 컴포넌트)

- **역할**: 모달의 기본 껍데기만 제공
- **특징**: 스타일 없음, children으로 내용 구성
- **위치**: `@commons/Modal/Modal.tsx`

```typescript
// 기본 사용법
<Modal isOpen={isOpen} onClose={onClose} customCSS={overlayStyles}>
  {/* 원하는 내용 */}
</Modal>
```

### 2. AlertModal

- **역할**: 단순 알림용 모달 (확인 버튼 1개)
- **사용 시기**: 정보 전달, 성공/실패 알림 등

```typescript
<AlertModal
  isOpen={isOpen}
  onClose={onClose}
  title="알림"
  message="작업이 완료되었습니다."
  onConfirm={handleConfirm}
  confirmText="확인"
/>
```

### 3. ConfirmModal

- **역할**: 사용자 확인이 필요한 모달 (취소/확인 버튼 2개)
- **사용 시기**: 삭제, 수정 등 중요한 액션 전 확인

```typescript
<ConfirmModal
  isOpen={isOpen}
  onClose={onClose}
  title="삭제 확인"
  message="정말로 삭제하시겠습니까?"
  onConfirm={handleDelete}
  confirmText="삭제"
  cancelText="취소"
  width={350}
  height={200}
/>
```

## 🔧 useModal 훅

### 현재 제공 기능

```typescript
const { handleOverlayClick, handleConfirm } = useModal({
  isOpen,
  onClose,
  onConfirm,
});
```

- **handleOverlayClick**: 오버레이 클릭 시 모달 닫기
- **handleConfirm**: 확인 버튼 클릭 시 onConfirm 실행 후 모달 닫기
- **ESC 키 처리**: 자동으로 ESC 키로 모달 닫기

## 🚀 권장 사용 패턴

### 1. 기본 Alert 사용

```typescript
function MyComponent() {
  const [isAlertOpen, setIsAlertOpen] = useState(false);

  const handleSuccess = () => {
    // 성공 처리 로직
    setIsAlertOpen(false);
  };

  return (
    <>
      <button onClick={() => setIsAlertOpen(true)}>
        작업 완료
      </button>

      <AlertModal
        isOpen={isAlertOpen}
        onClose={() => setIsAlertOpen(false)}
        title="성공"
        message="작업이 성공적으로 완료되었습니다."
        onConfirm={handleSuccess}
      />
    </>
  );
}
```

### 2. 기본 Confirm 사용

```typescript
function MyComponent() {
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);

  const handleDelete = () => {
    // 삭제 로직
    console.log('삭제 실행');
    setIsConfirmOpen(false);
  };

  return (
    <>
      <button onClick={() => setIsConfirmOpen(true)}>
        삭제
      </button>

      <ConfirmModal
        isOpen={isConfirmOpen}
        onClose={() => setIsConfirmOpen(false)}
        title="삭제 확인"
        message="이 항목을 삭제하시겠습니까?"
        onConfirm={handleDelete}
        confirmText="삭제"
      />
    </>
  );
}
```

## 🎨 커스텀 모달 만들기

기본 Modal 컴포넌트를 사용해서 완전히 새로운 모달을 만들 수 있습니다:

```typescript
function CustomModal({ isOpen, onClose }) {
  return (
    <Modal isOpen={isOpen} onClose={onClose} customCSS={myCustomStyles}>
      <div css={myModalContentStyles}>
        {/* 완전히 자유로운 커스텀 내용 */}
        <h1>커스텀 모달</h1>
        <form>
          {/* 폼 내용 */}
        </form>
        <div>
          <button onClick={onClose}>닫기</button>
        </div>
      </div>
    </Modal>
  );
}
```

## 🔮 향후 개선 예정 사항

### 커스텀 훅으로 모달 관리

```typescript
// 향후 구현 예정
const useAdminModal = () => {
  const [modalState, setModalState] = useState({ type: null, data: null });

  const openDeleteModal = (item) => {
    setModalState({ type: 'delete', data: item });
  };

  const openConfirmModal = (item) => {
    setModalState({ type: 'confirm', data: item });
  };

  const closeModal = () => {
    setModalState({ type: null, data: null });
  };

  const handleAction = () => {
    // 실제 비즈니스 로직 처리
    closeModal();
  };

  return {
    modalState,
    openDeleteModal,
    openConfirmModal,
    closeModal,
    handleAction,
  };
};
```

### 사용 예시

```typescript
function AdminPage() {
  const {
    modalState,
    openDeleteModal,
    openConfirmModal,
    closeModal,
    handleAction,
  } = useAdminModal();

  return (
    <>
      {/* 페이지 내용 */}

      {modalState.type === 'delete' && (
        <ConfirmModal
          isOpen={true}
          onClose={closeModal}
          title="삭제 확인"
          message="정말로 삭제하시겠습니까?"
          onConfirm={handleAction}
        />
      )}

      {modalState.type === 'confirm' && (
        <AlertModal
          isOpen={true}
          onClose={closeModal}
          title="완료 확인"
          onConfirm={handleAction}
        />
      )}
    </>
  );
}
```

## 📋 Props 참고

### Modal Props

```typescript
interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  customCSS?: SerializedStyles | SerializedStyles[];
}
```

### AlertModal Props

```typescript
interface AlertModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  message?: string;
  onConfirm?: () => void;
  confirmText?: string;
}
```

### ConfirmModal Props

```typescript
interface ConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  message?: string;
  onConfirm?: () => void;
  confirmText?: string;
  cancelText?: string;
  width?: number;
  height?: number;
}
```
