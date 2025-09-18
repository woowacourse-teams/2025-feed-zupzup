import { useAppTheme } from '@/hooks/useAppTheme';
import useCheerButton from '@/domains/hooks/useCheerButton';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import {
  headerContainer,
  headerText,
  headerCheerButton,
  cheerButtonLayout,
  titleText,
  panelCaption,
} from './OverviewHeader.styles';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import CheerButton from '../../CheerButton/CheerButton';

export default function OverviewHeader() {
  const theme = useAppTheme();
  const { organizationId } = useOrganizationId();
  const { groupName, totalCheeringCount } = useOrganizationName({
    organizationId,
  });

  const { handleCheerButton, animate } = useCheerButton({
    organizationId,
  });

  return (
    <div css={headerContainer}>
      <div css={headerText}>
        <p css={titleText(theme)}>{groupName}</p>
        <p css={panelCaption(theme)}>지금까지의 피드백</p>
      </div>
      <div css={headerCheerButton}>
        <div css={cheerButtonLayout}>
          <CheerButton
            totalCheeringCount={totalCheeringCount}
            onClick={handleCheerButton}
            animate={animate}
          />
        </div>
      </div>
    </div>
  );
}
