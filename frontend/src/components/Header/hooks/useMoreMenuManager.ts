import { useEffect, useRef, useState } from 'react';

export default function useMoreMenuManager() {
  const [isOpenMoreMenu, setIsOpenMoreMenu] = useState(false);
  const moreButtonRef = useRef<HTMLElement | null>(null);

  useEffect(() => {
    if (!isOpenMoreMenu) return;

    const onPointerDown = (e: PointerEvent) => {
      const target = e.target as Node;

      if (moreButtonRef.current && moreButtonRef.current.contains(target)) {
        return;
      }

      setIsOpenMoreMenu(false);
    };

    window.addEventListener('pointerdown', onPointerDown);
    return () => {
      window.removeEventListener('pointerdown', onPointerDown);
    };
  }, [isOpenMoreMenu]);

  const toggleMoreMenu = () => {
    setIsOpenMoreMenu((prev) => !prev);
  };

  return { isOpenMoreMenu, toggleMoreMenu, moreButtonRef };
}
