# Modal 컴포넌트 사용법 가이드

## 📁 파일 구조

```
frontend/src/components/
├── Modal/
│   ├── Modal.tsx                # 공통 Modal 컴포넌트
│   └── Modal.styles.ts          # 공통 Modal 스타일 (overlay, modalBox)
├── AlertModal/
│   ├── AlertModal.tsx           # Alert 모달 컴포넌트
│   └── AlertModal.styles.ts     # Alert 모달 스타일 (간소화됨)
└── ConfirmModal/
    ├── ConfirmModal.tsx         # Confirm 모달 컴포넌트
    └── ConfirmModal.styles.ts   # Confirm 모달 스타일 (간소화됨)

frontend/src/hooks/
└── useModal.ts                  # 모달 공통 로직 훅
```

## 🎯 컴포넌트별 역할

### 1. Modal (공통 컴포넌트) - **개선됨** ✨

- **역할**: 모달의 overlay와 기본 박스까지 제공
- **특징**: 기본 overlay + 흰색 모달 박스 자동 제공, children으로 내용만 구성

```typescript
// 기본 사용법 - overlay와 모달 박스는 자동 제공!
<Modal onClose={onClose}>
  {/* 모달 내용만 작성하면 됨 */}
  <h2>제목</h2>
  <p>내용</p>
  <button>버튼</button>
</Modal>

// 크기 조절
<Modal onClose={onClose} width={400} height={300}>
  {/* 내용 */}
</Modal>
```

### 2. AlertModal - **단순화됨** ✨

- **역할**: 단순 알림용 모달 (확인 버튼 1개)
- **변경점**: overlay, modal 박스 스타일 제거 → 내용 스타일만 관리
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

### 3. ConfirmModal - **단순화됨** ✨

- **역할**: 사용자 확인이 필요한 모달 (취소/확인 버튼 2개)
- **변경점**: overlay, modal 박스 스타일 제거 → 내용 스타일만 관리
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
const { handleOverlayClick } = useModal({
  isOpen,
  onClose,
});
```

- **handleOverlayClick**: 오버레이 클릭 시 모달 닫기 (모달 박스 클릭 시는 닫히지
  않음)
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

## 🔮 향후 개선 예정 사항

### 커스텀 훅으로 모달 관리

```typescript
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

### Modal Props - **업데이트됨** ✨

```typescript
interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  width?: number; // 기본값: 300
  height?: number; // 선택적
  customCSS?: SerializedStyles | SerializedStyles[]; // 모달 박스 추가 스타일링
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
  confirmText?: string; // 기본값: '확인'
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
  confirmText?: string; // 기본값: '확인'
  cancelText?: string; // 기본값: '취소'
  width?: number; // 기본값: 300
  height?: number; // 선택적
}
```
