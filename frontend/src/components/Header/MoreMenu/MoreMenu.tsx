import { moreMenuContainer } from '@/components/Header/MoreMenu/MoreMenu.styles';
import MoreMenuItem from '@/components/Header/MoreMenuItem/MoreMenuItem';
import ShareIcon from '@/components/icons/ShareIcon';
import SmallSettingIcon from '@/components/icons/SmallSettingIcon';

const moreMenuList = [
  { icon: SmallSettingIcon, menu: '방정보 수정' },
  { icon: ShareIcon, menu: 'QR/URL 공유' },
];

export default function MoreMenu() {
  return (
    <div css={moreMenuContainer}>
      {moreMenuList.map((item, index) => (
        <MoreMenuItem key={index} icon={<item.icon />} menu={item.menu} />
      ))}
    </div>
  );
}
