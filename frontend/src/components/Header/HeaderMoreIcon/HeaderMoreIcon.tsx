import Button from '@/components/@commons/Button/Button';
import MoreMenu from '@/components/Header/MoreMenu/MoreMenu';
import useMoreMenuManager from '@/components/Header/hooks/useMoreMenuManager';
import MoreVerticalIcon from '@/components/icons/MoreVerticalIcon';
import { useAppTheme } from '@/hooks/useAppTheme';
import {
  MoreButton,
  moreMenu,
  moreMenuContainer,
} from './HeaderMoreIcon.styles';
import useDownloadFeedbacks from '@/components/Header/hooks/useDownloadFeedbacks';

export default function HeaderMoreIcon() {
  const theme = useAppTheme();
  const { isOpenMoreMenu, toggleMoreMenu, moreButtonRef, closeMoreMenu } =
    useMoreMenuManager();
  const { feedbackDownloadStatus, setJobId } = useDownloadFeedbacks();

  return (
    <div
      css={moreMenuContainer(theme)}
      ref={moreButtonRef as React.RefObject<HTMLDivElement>}
    >
      <Button onClick={toggleMoreMenu} customCSS={MoreButton}>
        <MoreVerticalIcon />
      </Button>
      {isOpenMoreMenu && (
        <div css={moreMenu}>
          <MoreMenu
            closeMoreMenu={closeMoreMenu}
            setJobId={setJobId}
            feedbackDownloadStatus={feedbackDownloadStatus}
          />
        </div>
      )}
    </div>
  );
}
