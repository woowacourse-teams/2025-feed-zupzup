import { useEffect, useRef, useState } from 'react';

export default function useMoreMenuManager() {
  const [isOpenMoreMenu, setIsOpenMoreMenu] = useState(false);
  const moreButtonRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!isOpenMoreMenu) return;

    const handlePointerDown = (e: PointerEvent) => {
      const target = e.target as Node;

      if (moreButtonRef.current?.contains(target)) return;

      setIsOpenMoreMenu(false);
    };

    window.addEventListener('pointerdown', handlePointerDown);

    return () => {
      window.removeEventListener('pointerdown', handlePointerDown);
    };
  }, [isOpenMoreMenu]);

  const toggleMoreMenu = () => {
    setIsOpenMoreMenu((prev) => !prev);
  };

  const closeMoreMenu = () => {
    setIsOpenMoreMenu(false);
  };

  return { isOpenMoreMenu, toggleMoreMenu, moreButtonRef, closeMoreMenu };
}
