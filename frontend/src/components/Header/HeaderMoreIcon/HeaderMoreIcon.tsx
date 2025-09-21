import Button from '@/components/@commons/Button/Button';
import MoreVerticalIcon from '@/components/icons/MoreVerticalIcon';
import MoreMenu from '@/components/Header/MoreMenu/MoreMenu';
import useMoreMenuManager from '@/components/Header/hooks/useMoreMenuManager';
import {
  moreMenuContainer,
  moreMenu,
  MoreButton,
} from './HeaderMoreIcon.styles';

export default function HeaderMoreIcon() {
  const { isOpenMoreMenu, toggleMoreMenu, moreButtonRef, closeMoreMenu } =
    useMoreMenuManager();

  return (
    <div
      css={moreMenuContainer}
      ref={moreButtonRef as React.RefObject<HTMLDivElement>}
    >
      <Button onClick={toggleMoreMenu} customCSS={MoreButton}>
        <MoreVerticalIcon />
      </Button>
      {isOpenMoreMenu && (
        <div css={moreMenu}>
          <MoreMenu closeMoreMenu={closeMoreMenu} />
        </div>
      )}
    </div>
  );
}
